require("dotenv").config();
const express = require("express");
const { startConsumer } = require("./infra/kafka.consumer");

const app = express();
app.use(express.json());

// Start Kafka consumer on boot
startConsumer()
  .then(() => console.log("Kafka consumer initialized"))
  .catch(err => {
    console.error("Kafka consumer failed", err);
    process.exit(1);
  });

app.listen(process.env.PORT, () => {
  console.log(`Mail service running on port ${process.env.PORT}`);
});
