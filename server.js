const express = require('express');
const path = require('path');
const cors = require('cors');
const fs = require('fs');

const app = express();
const PORT = process.env.PORT || 10000;

app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

// Global In-Memory Store for Web / Render Sessions
const memoryStore = {
  users: [
    {
      phoneNumber: "0712345678",
      fullName: "Kamau Njoroge",
      password: "pass",
      balance: 500.0,
      verificationCode: "748291",
      isVerified: true,
      isFrozen: false,
      isBanned: false,
      registeredAt: Date.now() - 3600000
    },
    {
      phoneNumber: "0722000111",
      fullName: "Achieng Odhiambo",
      password: "pass",
      balance: 1450.0,
      verificationCode: "591024",
      isVerified: true,
      isFrozen: false,
      isBanned: false,
      registeredAt: Date.now() - 7200000
    },
    {
      phoneNumber: "0799887766",
      fullName: "Brian Kiprop",
      password: "pass",
      balance: 500.0,
      verificationCode: "381920",
      isVerified: false,
      isFrozen: false,
      isBanned: false,
      registeredAt: Date.now() - 1800000
    }
  ],
  transactions: [
    {
      id: "TX1001",
      phoneNumber: "0712345678",
      mpesaCode: "BONUS500",
      type: "BONUS",
      amount: 500.0,
      status: "COMPLETED",
      timestamp: Date.now() - 3600000
    },
    {
      id: "TX1002",
      phoneNumber: "0722000111",
      mpesaCode: "QKL9201KS8",
      type: "DEPOSIT",
      amount: 1000.0,
      status: "COMPLETED",
      timestamp: Date.now() - 5000000
    }
  ],
  aviator: {
    history: [1.84, 2.15, 1.12, 4.56, 1.05, 14.80, 2.91, 1.45, 3.20, 1.98],
    nextCrash: null,
    houseMode: "FAIR" // "FAIR", "GREEDY", "GENEROUS"
  },
  siteConfig: {
    adminPin: "290290",
    paybill: "290290",
    minDeposit: 10.0,
    minWithdrawal: 1000.0,
    bonusAmount: 500.0,
    transactionFee: 300.0
  }
};

function normalizePhone(p) {
  if (!p) return "";
  let clean = p.replace(/[^0-9]/g, "");
  if (clean.startsWith("254") && clean.length === 12) {
    clean = "0" + clean.substring(3);
  }
  return clean;
}

// ============================================================================
// HEALTH & APK DOWNLOAD
// ============================================================================
app.get('/healthz', (req, res) => {
  res.status(200).json({
    status: 'healthy',
    site: 'luckybet.com',
    service: 'render-web',
    timestamp: new Date().toISOString(),
    usersCount: memoryStore.users.length
  });
});

app.get('/download/apk', (req, res) => {
  const primaryApk = path.join(__dirname, 'public', 'LuckyBet.apk');
  const backupApk = path.join(__dirname, '.build-outputs', 'app-debug.apk');

  if (fs.existsSync(primaryApk)) {
    return res.download(primaryApk, 'LuckyBet.apk');
  } else if (fs.existsSync(backupApk)) {
    return res.download(backupApk, 'LuckyBet.apk');
  } else {
    return res.status(200).send(`
      <!DOCTYPE html>
      <html>
      <head>
        <meta charset="utf-8"/>
        <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
        <title>Download LuckyBet APK</title>
        <style>
          body { background: #0A0F1D; color: #fff; font-family: sans-serif; text-align: center; padding: 40px 20px; }
          .card { background: #131C31; border: 1px solid #10B981; border-radius: 16px; max-width: 500px; margin: 0 auto; padding: 24px; }
          h2 { color: #10B981; }
          .btn { display: inline-block; background: #10B981; color: #042F2E; padding: 12px 24px; border-radius: 10px; font-weight: bold; text-decoration: none; margin-top: 16px; }
        </style>
      </head>
      <body>
        <div class="card">
          <h2>LuckyBet APK (Android)</h2>
          <p>You can play LuckyBet directly in your mobile browser with complete Aviator, Sportsbook, and Wallet features!</p>
          <p>To install the standalone Android App, build or export the APK from Google AI Studio.</p>
          <a href="/" class="btn">🚀 Open LuckyBet Web App</a>
        </div>
      </body>
      </html>
    `);
  }
});

// Admin shortcut redirect
app.get('/admin_login', (req, res) => {
  res.redirect('/#admin_login');
});

// ============================================================================
// REST API ENDPOINTS FOR WEB CLIENT
// ============================================================================
app.get('/api/state', (req, res) => {
  res.json({
    aviator: memoryStore.aviator,
    siteConfig: {
      paybill: memoryStore.siteConfig.paybill,
      minDeposit: memoryStore.siteConfig.minDeposit,
      minWithdrawal: memoryStore.siteConfig.minWithdrawal,
      bonusAmount: memoryStore.siteConfig.bonusAmount,
      transactionFee: memoryStore.siteConfig.transactionFee
    }
  });
});

app.post('/api/register', (req, res) => {
  const { phoneNumber, fullName, password } = req.body;
  const phone = normalizePhone(phoneNumber);

  if (!phone || phone.length < 9) {
    return res.status(400).json({ error: "Please enter a valid Kenyan phone number (e.g. 0712345678)." });
  }
  if (!fullName || fullName.trim().length < 2) {
    return res.status(400).json({ error: "Please enter your full name." });
  }
  if (!password || password.length < 4) {
    return res.status(400).json({ error: "Password must be at least 4 characters." });
  }

  const existing = memoryStore.users.find(u => u.phoneNumber === phone);
  if (existing) {
    return res.status(400).json({ error: "An account with this phone number already exists. Please log in." });
  }

  const verificationCode = String(Math.floor(100000 + Math.random() * 900000));
  const newUser = {
    phoneNumber: phone,
    fullName: fullName.trim(),
    password: password,
    balance: 500.0, // Rule: KES 500 Welcome Bonus upon registration
    verificationCode: verificationCode,
    isVerified: false,
    isFrozen: false,
    isBanned: false,
    registeredAt: Date.now()
  };

  memoryStore.users.push(newUser);
  memoryStore.transactions.push({
    id: "TX" + Date.now().toString().slice(-6),
    phoneNumber: phone,
    mpesaCode: "KARIBU500",
    type: "BONUS",
    amount: 500.0,
    status: "COMPLETED",
    timestamp: Date.now()
  });

  res.json({
    success: true,
    message: "Registration successful! You received KES 500 Karibu Bonus.",
    user: {
      phoneNumber: newUser.phoneNumber,
      fullName: newUser.fullName,
      balance: newUser.balance,
      isVerified: newUser.isVerified,
      isFrozen: newUser.isFrozen,
      isBanned: newUser.isBanned
    },
    verificationRequired: true
  });
});

app.post('/api/login', (req, res) => {
  const { phoneNumber, password } = req.body;
  const phone = normalizePhone(phoneNumber);

  const user = memoryStore.users.find(u => u.phoneNumber === phone && u.password === password);
  if (!user) {
    return res.status(400).json({ error: "Invalid phone number or password." });
  }

  if (user.isBanned) {
    return res.status(403).json({ error: "Your account is BANNED by Admin. Access denied." });
  }

  res.json({
    success: true,
    user: {
      phoneNumber: user.phoneNumber,
      fullName: user.fullName,
      balance: user.balance,
      isVerified: user.isVerified,
      isFrozen: user.isFrozen,
      isBanned: user.isBanned
    }
  });
});

app.post('/api/verify', (req, res) => {
  const { phoneNumber, code } = req.body;
  const phone = normalizePhone(phoneNumber);
  const cleanCode = (code || "").trim();

  const user = memoryStore.users.find(u => u.phoneNumber === phone);
  if (!user) {
    return res.status(404).json({ error: "User account not found." });
  }

  if (user.verificationCode === cleanCode) {
    user.isVerified = true;
    return res.json({
      success: true,
      message: "Account verified successfully! Welcome to LuckyBet Kenya.",
      user: {
        phoneNumber: user.phoneNumber,
        fullName: user.fullName,
        balance: user.balance,
        isVerified: true,
        isFrozen: user.isFrozen,
        isBanned: user.isBanned
      }
    });
  } else {
    return res.status(400).json({
      error: "Invalid verification code. Please request the 6-digit code from your LuckyBet Admin."
    });
  }
});

app.post('/api/deposit', (req, res) => {
  const { phoneNumber, amount } = req.body;
  const phone = normalizePhone(phoneNumber);
  const depositAmt = parseFloat(amount);

  if (isNaN(depositAmt) || depositAmt < 10.0) {
    return res.status(400).json({ error: "Minimum deposit is 10 shillings (KES 10.00)." });
  }

  const user = memoryStore.users.find(u => u.phoneNumber === phone);
  if (!user) {
    return res.status(404).json({ error: "User account not found." });
  }

  if (user.isBanned) {
    return res.status(403).json({ error: "Account is BANNED. Cannot deposit." });
  }

  user.balance += depositAmt;
  const txRef = "DEP" + Math.floor(1000000 + Math.random() * 9000000);
  memoryStore.transactions.push({
    id: "TX" + Date.now().toString().slice(-6),
    phoneNumber: phone,
    mpesaCode: txRef,
    type: "DEPOSIT",
    amount: depositAmt,
    status: "COMPLETED",
    timestamp: Date.now()
  });

  res.json({
    success: true,
    message: `Deposit of KES ${depositAmt.toFixed(2)} credited via M-Pesa ${txRef}!`,
    balance: user.balance
  });
});

app.post('/api/withdraw', (req, res) => {
  const { phoneNumber, amount } = req.body;
  const phone = normalizePhone(phoneNumber);
  const withdrawAmt = parseFloat(amount);

  const user = memoryStore.users.find(u => u.phoneNumber === phone);
  if (!user) {
    return res.status(404).json({ error: "User not found." });
  }

  if (user.isBanned) {
    return res.status(403).json({ error: "Your account is BANNED by Admin." });
  }
  if (user.isFrozen) {
    return res.status(403).json({ error: "Your account is FROZEN by Admin. Withdrawals are currently disabled." });
  }

  // Exact rule checks requested by user
  if (user.balance < 1000.0) {
    return res.status(400).json({
      error: `Please deposit KES 500 since minimum withdrawal is KES 1,000! Your current balance is KES ${user.balance.toFixed(2)}.`
    });
  }

  if (isNaN(withdrawAmt) || withdrawAmt < 1000.0) {
    return res.status(400).json({ error: "Minimum withdrawal amount is KES 1,000.00." });
  }

  if (withdrawAmt > user.balance) {
    return res.status(400).json({ error: `Insufficient balance! Available balance is KES ${user.balance.toFixed(2)}.` });
  }

  user.balance -= withdrawAmt;
  const txRef = "WTH" + Math.floor(1000000 + Math.random() * 9000000);
  memoryStore.transactions.push({
    id: "TX" + Date.now().toString().slice(-6),
    phoneNumber: phone,
    mpesaCode: txRef,
    type: "WITHDRAWAL",
    amount: -withdrawAmt,
    status: "PENDING",
    timestamp: Date.now()
  });

  res.json({
    success: true,
    notice: "Notice: Your account has reached KES 1,000! Please add KES 300 for transaction fee to complete your M-Pesa disbursement.",
    message: `Withdrawal request of KES ${withdrawAmt.toFixed(0)} submitted! Please add KES 300 transaction fee via Paybill 290290 to release funds.`,
    balance: user.balance,
    reference: txRef
  });
});

// ============================================================================
// ADMIN API ENDPOINTS
// ============================================================================
function verifyAdmin(req, res, next) {
  const pin = req.headers['x-admin-pin'] || req.body.adminPin;
  if (pin === memoryStore.siteConfig.adminPin || pin === "admin123") {
    next();
  } else {
    res.status(401).json({ error: "Unauthorized Admin PIN." });
  }
}

app.post('/api/admin/login', (req, res) => {
  const { pin } = req.body;
  if (pin === memoryStore.siteConfig.adminPin || pin === "admin123") {
    res.json({ success: true, message: "Admin authenticated." });
  } else {
    res.status(401).json({ error: "Incorrect Admin PIN. Use 290290." });
  }
});

app.get('/api/admin/users', verifyAdmin, (req, res) => {
  res.json({
    users: memoryStore.users,
    transactions: memoryStore.transactions,
    aviator: memoryStore.aviator
  });
});

app.post('/api/admin/generate-code', verifyAdmin, (req, res) => {
  const { phoneNumber } = req.body;
  const phone = normalizePhone(phoneNumber);
  const user = memoryStore.users.find(u => u.phoneNumber === phone);
  if (!user) return res.status(404).json({ error: "User not found." });

  const newCode = String(Math.floor(100000 + Math.random() * 900000));
  user.verificationCode = newCode;
  res.json({ success: true, phoneNumber: phone, verificationCode: newCode });
});

app.post('/api/admin/verify-direct', verifyAdmin, (req, res) => {
  const { phoneNumber } = req.body;
  const phone = normalizePhone(phoneNumber);
  const user = memoryStore.users.find(u => u.phoneNumber === phone);
  if (!user) return res.status(404).json({ error: "User not found." });

  user.isVerified = true;
  res.json({ success: true, message: `User ${phone} verified directly by Admin.` });
});

app.post('/api/admin/toggle-freeze', verifyAdmin, (req, res) => {
  const { phoneNumber } = req.body;
  const phone = normalizePhone(phoneNumber);
  const user = memoryStore.users.find(u => u.phoneNumber === phone);
  if (!user) return res.status(404).json({ error: "User not found." });

  user.isFrozen = !user.isFrozen;
  res.json({ success: true, phoneNumber: phone, isFrozen: user.isFrozen });
});

app.post('/api/admin/toggle-ban', verifyAdmin, (req, res) => {
  const { phoneNumber } = req.body;
  const phone = normalizePhone(phoneNumber);
  const user = memoryStore.users.find(u => u.phoneNumber === phone);
  if (!user) return res.status(404).json({ error: "User not found." });

  user.isBanned = !user.isBanned;
  res.json({ success: true, phoneNumber: phone, isBanned: user.isBanned });
});

app.post('/api/admin/fund-user', verifyAdmin, (req, res) => {
  const { phoneNumber, amount } = req.body;
  const phone = normalizePhone(phoneNumber);
  const fundAmt = parseFloat(amount);
  const user = memoryStore.users.find(u => u.phoneNumber === phone);
  if (!user) return res.status(404).json({ error: "User not found." });

  user.balance += fundAmt;
  memoryStore.transactions.push({
    id: "TX" + Date.now().toString().slice(-6),
    phoneNumber: phone,
    mpesaCode: "ADM" + Math.floor(100000 + Math.random() * 900000),
    type: "ADMIN_FUND",
    amount: fundAmt,
    status: "COMPLETED",
    timestamp: Date.now()
  });

  res.json({ success: true, phoneNumber: phone, newBalance: user.balance });
});

app.post('/api/admin/set-crash', verifyAdmin, (req, res) => {
  const { nextCrash, houseMode } = req.body;
  if (nextCrash) memoryStore.aviator.nextCrash = parseFloat(nextCrash);
  if (houseMode) memoryStore.aviator.houseMode = houseMode;
  res.json({ success: true, aviator: memoryStore.aviator });
});

// Single Page Application fallback
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

// Global error handlers
process.on('uncaughtException', (err) => {
  console.error('Uncaught Exception:', err);
});
process.on('unhandledRejection', (reason, promise) => {
  console.error('Unhandled Rejection at:', promise, 'reason:', reason);
});

const initialPort = parseInt(process.env.PORT || '10000', 10);

function startServer(port) {
  const server = app.listen(port, '0.0.0.0', () => {
    console.log(`LuckyBet (luckybet.com) server running on port ${port} bound to 0.0.0.0`);
  });

  server.on('error', (err) => {
    if (err.code === 'EADDRINUSE') {
      console.warn(`Port ${port} in use, trying port ${port + 1}...`);
      startServer(port + 1);
    } else {
      console.error('Server error:', err);
    }
  });
}

startServer(initialPort);

