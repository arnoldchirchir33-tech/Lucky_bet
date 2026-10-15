const express = require('express');
const path = require('path');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 10000;

app.use(cors());
app.use(express.json());
app.use(express.static(path.join(__dirname, 'public')));

// Fallback APK route
app.get('/download/apk', (req, res) => {
  const apkPath = path.join(__dirname, '.build-outputs', 'app-debug.apk');
  res.download(apkPath, 'LuckyBet.apk', (err) => {
    if (err) {
      res.status(404).send('APK build in progress or not found. Please export from AI Studio.');
    }
  });
});

// Admin redirect shortcut
app.get('/admin_login', (req, res) => {
  res.redirect('/#admin_login');
});

// Health check for Render
app.get('/healthz', (req, res) => {
  res.status(200).json({ status: 'healthy', site: 'luckybet.com' });
});

// Catch-all route to serve the interactive web app
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'index.html'));
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`LuckyBet (luckybet.com) server running on port ${PORT}`);
});
