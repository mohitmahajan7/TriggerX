const { Kafka } = require("kafkajs");
const { sendOtpMail } = require("../services/mail.service");

const kafka = new Kafka({
  clientId: "triggerx-mail-service",
  brokers: ["localhost:9092"]
});

const consumer = kafka.consumer({ groupId: "triggerx-mail-group" });

const startConsumer = async () => {
  await consumer.connect();
  await consumer.subscribe({ topic: "email-otp-events" });

  console.log("Kafka consumer started");

  await consumer.run({
    eachMessage: async ({ message }) => {
      const payload = JSON.parse(message.value.toString());
      const { email, otp } = payload;

      await sendOtpMail(email, otp);
    }
  });
};

module.exports = { startConsumer };
