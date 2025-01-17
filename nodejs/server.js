import 'dotenv/config';
import express from 'express';

const { PORT, CRATE_URL } = process.env;

// Initialize Express.
const app = express();
app.use(express.static('static'));
app.use(express.json());

app.get('/api/windfarms', async (req, res) => {
  res.sendStatus('TODO');
});

app.get('/api/latest/:id', async (req, res) => {
  res.sendStatus('TODO');
});

app.get('/api/avgpctformonth/:id/:ts', async (req, res) => {
  res.sendStatus('TODO');
});

app.get('/api/outputforday/:id/:ts', async (req, res) => {
  res.sendStatus('TODO');
});

app.get('/api/dailymaxpct/:id/:days', async (req, res) => {
  res.sendStatus('TODO');
});

// 
// Start the Express server.
app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}.`);
});