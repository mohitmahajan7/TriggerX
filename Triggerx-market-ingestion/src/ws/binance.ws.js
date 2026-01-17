const WebSocket = require("ws");
const { sendMarketTick } = require("../producers/marketTick.producer");

const BINANCE_WS_URL =
  "wss://stream.binance.com:9443/ws/btcusdt@trade";

const startBinanceStream = () => {
  const ws = new WebSocket(BINANCE_WS_URL);

  ws.on("open", () => {
    console.log("Connected to Binance WebSocket");
  });

  ws.on("message", async (data) => {
    const msg = JSON.parse(data.toString());

    const tick = {
      symbol: msg.s,
      price: parseFloat(msg.p),
      timestamp: new Date(msg.T).toISOString(),
    };

    console.log("Market tick:", tick);

    await sendMarketTick(tick);
  });

  ws.on("close", () => {
    console.log("Binance WS closed. Reconnecting...");
    setTimeout(startBinanceStream, 3000);
  });

  ws.on("error", (err) => {
    console.error("Binance WS error", err.message);
    ws.close();
  });
};

module.exports = { startBinanceStream };
