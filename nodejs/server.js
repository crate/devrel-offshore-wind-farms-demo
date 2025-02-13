import 'dotenv/config';
import express from 'express';
import pg from 'pg';

const { PORT, CRATEDB_URL } = process.env;
const { Client } = pg;

// Connect to CrateDB.
const client = new Client(CRATEDB_URL);
await client.connect();

const res = await client.query('SELECT * FROM windfarms WHERE territory=$1', ['Scotland']);

for (const windFarm of res.rows) {
  console.log(windFarm);
}

// Run a SQL statement in CrateDB and capture the response.
async function executeSQL(sqlStmt) {
  // TODO
}

// Initialize Express.
const app = express();
app.use(express.static('static'));
app.use(express.json());

app.get('/api/windfarms', async (req, res) => {
  // "SELECT id, name, description, location, boundaries, turbines FROM windfarms ORDER BY id ASC"
  res.sendStatus('TODO');
});

app.get('/api/latest/:id', async (req, res) => {
  // "SELECT ts, day, month, output, outputpercentage FROM windfarm_output WHERE windfarmid = ? ORDER BY ts DESC LIMIT 1"
  res.sendStatus('TODO');
});

app.get('/api/avgpctformonth/:id/:ts', async (req, res) => {
  // "SELECT trunc(avg(outputpercentage), 2) FROM windfarm_output WHERE windfarmid = ? and month = ?"
  res.sendStatus('TODO');
});

app.get('/api/outputforday/:id/:ts', async (req, res) => {
  // "SELECT extract(hour from ts) as hour, output, sum(output) OVER (ORDER BY ts ASC) FROM windfarm_output WHERE windfarmid = ? AND day = ? ORDER BY hour ASC"
  res.sendStatus('TODO');
});

app.get('/api/dailymaxpct/:id/:days', async (req, res) => {
  // "SELECT day, max(outputpercentage) FROM windfarm_output WHERE windfarmid = ? GROUP BY day ORDER BY day DESC LIMIT ?"
  res.sendStatus('TODO');
});

// Start the Express server.
app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}.`);
});