const { producer } = require("../config/kafka");

const TOPIC = "market-ticks";

const sendMarketTick = async (tick) => {
  await producer.send({
    topic: TOPIC,
    messages: [
      {
        key: tick.symbol,
        value: JSON.stringify(tick),
      },
    ],
  });
};

module.exports = { sendMarketTick };
