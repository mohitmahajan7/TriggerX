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

module.exports = { sendOtpMail };
