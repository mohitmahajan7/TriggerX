const transporter = require("../config/mail.config");

const sendOtpMail = async (email, otp) => {
  await transporter.sendMail({
    from: `"TriggerX" <${process.env.SMTP_USER}>`,
    to: email,
    subject: "Your TriggerX OTP",
    html: `
      <h2>TriggerX Verification</h2>
      <p>Your OTP is <b>${otp}</b></p>
      <p>Valid for 5 minutes</p>
    `
  });
};

const sendWelcomeMail = async (email) => {
  await transporter.sendMail({
    from: `"TriggerX" <${process.env.SMTP_USER}>`,
    to: email,
    subject: "Welcome to TriggerX :)",
    html: `
      <h2>Welcome to TriggerX!</h2>
      <p>Hi there!!</p>
      <p>Thank you for registering with <b>TriggerX</b>.</p>
      <p>You can now start automating actions based on events.</p>
      <p>This is just the beginning..</p>
      <p>— Team TriggerX</p>
    `
  });
};

module.exports = { sendOtpMail, sendWelcomeMail };
