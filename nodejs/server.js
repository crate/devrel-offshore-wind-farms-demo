import 'dotenv/config';
import express from 'express';
import pg from 'pg';

const { PORT } = process.env;
const { Pool } = pg;

// Connect to CrateDB.
const pool = new Pool();

pool.on('error', (err, client) => {
  console.error('Unexpected error in idle database client:', err);
});


// TODO remove this example query...
//const res = await client.query('SELECT * FROM windfarms WHERE territory=$1', ['Scotland']);

//console.log(res);
// for (const windFarm of res.rows) {
//   console.log(windFarm);
// }

// Initialize Express.
const app = express();
app.use(express.static('static'));
app.use(express.json());

app.get('/api/windfarms', async (req, res) => {
  const resultSet = await pool.query(
    'SELECT id, name, description, location, boundaries, turbines FROM windfarms ORDER BY id ASC'
  );

  if (resultSet.rowCount === 0) {
    return res.sendStatus(404).send('No windfarm data found.');
  }

  res.json({ results: resultSet.rows });
});

app.get('/api/latest/:id', async (req, res) => {
  const resultSet = await pool.query(
    'SELECT ts AS timestamp, day, month, output, outputpercentage FROM windfarm_output WHERE windfarmid = $1 ORDER BY ts DESC LIMIT 1',
    [ req.params.id ]
  );

  if (resultSet.rowCount === 0) {
    return res.status(404).send(`No such windfarm ID: ${req.params.id}.`);
  }

  res.json({ results: {
    timestamp: resultSet.rows[0].timestamp.getTime(),
    day: resultSet.rows[0].day.getTime(),
    month: resultSet.rows[0].month.getTime(),
    output: resultSet.rows[0].output,
    outputPercentage: resultSet.rows[0].outputpercentage,
  }});
});

app.get('/api/avgpctformonth/:id/:ts', async (req, res) => {
  const resultSet = await pool.query(
    'SELECT trunc(avg(outputpercentage), 2) AS avgoutputpct FROM windfarm_output WHERE windfarmid = $1 and month = $2',
    [ req.params.id, req.params.ts ]
  );

  const avgOutputPct = resultSet.rowCount === 0 ? null : resultSet.rows[0].avgoutputpct;

  if (avgOutputPct === null) {
    return res.status(404).send(`No such windfarm ID: ${req.params.id}.`);
  }

  res.json({ results: [ { avgPct: resultSet.rows[0].avgoutputpct }] });
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