const redis = require("redis");

const client = redis.createClient({url: "redis://localhost:6379"});

client.on("error", (err)=> {console.log("Redis error",err)});

(async () => {
    await client.connect();
    console.log("Redis connected");
}) ();

module.exports = client;
