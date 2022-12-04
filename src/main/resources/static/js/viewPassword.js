const passwordField = document.querySelector("#password");
const eyeIcon = document.querySelector("#eye");

const passwordField2 = document.querySelector("#password2");
const eyeIcon2 = document.querySelector("#eye2");

eye.addEventListener("click", function(){
  this.classList.toggle("fa-eye-slash");
  const type = passwordField.getAttribute("type") === "password" ? "text" : "password";
  passwordField.setAttribute("type", type);
})

eye2.addEventListener("click", function(){
  this.classList.toggle("fa-eye-slash");
  const type2 = passwordField2.getAttribute("type") === "password" ? "text" : "password";
  passwordField2.setAttribute("type", type2);
})