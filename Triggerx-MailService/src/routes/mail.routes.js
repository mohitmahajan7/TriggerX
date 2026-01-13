const express = require("express");
const { sendTestMail } = require("../controllers/mail.controller");

const router = express.Router();

router.post("/test", sendTestMail);

module.exports = router;
