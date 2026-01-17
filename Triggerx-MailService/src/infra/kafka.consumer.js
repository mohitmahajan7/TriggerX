const { Kafka } = require("kafkajs");
const { sendOtpMail, sendWelcomeMail } = require("../services/mail.service");

const kafka = new Kafka({
  clientId: "triggerx-mail-service",
  brokers: ["localhost:9092"]
});

const consumer = kafka.consumer({ groupId: "triggerx-mail-group" });

const startConsumer = async () => {
  await consumer.connect();

  await consumer.subscribe({ topic: "email-otp-events" });
  await consumer.subscribe({ topic: "user-events" });

  console.log("Kafka consumer started");

  await consumer.run({
    eachMessage: async ({ topic, message }) => {
      const payload = JSON.parse(message.value.toString());

      // OTP emails
      if (topic === "email-otp-events") {
        const { email, otp } = payload;
        await sendOtpMail(email, otp);
      }

      // Welcome email
      if (topic === "user-events" && payload.eventType === "USER_REGISTERED") {
        await sendWelcomeMail(payload.email);
      }
    }
  });
};

module.exports = { startConsumer };
