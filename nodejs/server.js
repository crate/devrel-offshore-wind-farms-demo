import 'dotenv/config';
import express from 'express';

const { PORT, CRATE_URL } = process.env;

// Initialize Express.
const app = express();
app.use(express.static('static'));
app.use(express.json());


// Start the Express server.
app.listen(PORT, () => {
  console.log(`Server listening on port ${PORT}.`);
});