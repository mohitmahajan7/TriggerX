require("dotenv").config();
const { producer } = require("./config/kafka");
const { startBinanceStream } = require("./ws/binance.ws");

const start = async () => {
  try {
    await producer.connect();
    console.log("Kafka producer connected");

    startBinanceStream();
  } catch (err) {
    console.error("Startup failed", err);
    process.exit(1);
  }
};

start();
