const mailService = require("../services/mail.service");

const sendTestMail = async (req, res) => {
  const { email } = req.body;

  await mailService.sendMail({
    to: email,
    subject: "TriggerX SMTP Test",
    html: "<h2>SMTP is working !!!</h2>"
  });

  res.json({ message: "Email sent successfully" });
};

module.exports = { sendTestMail };
