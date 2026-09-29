import {logInUser} from "./api.js"

const loginForm = document.getElementById("loginForm");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");

const emailError = document.getElementById("emailError");
const passwordError = document.getElementById("passwordError");

function validateLogin(){
    let isValid = true;

    emailError.textContent = "";
    passwordError.textContent ="";

    const email = emailInput.value.trim();
    if(email===""){
        emailError.textContent="Email is required";
        isValid = false;
    }
    else{
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if(!emailPattern.test(email)){
            emailError.textContent = "Please enter a valid email address";
            isValid = false;
        }
    }
    const password = passwordInput.value.trim();
    if(password===""){
        passwordError.textContent="Password is required";
        isValid = false;
    }
    else if(password.length<6){
        passwordError.textContent = "Password must contain at least 6 characters";
        isValid = false;
    }
    return isValid;
}

loginForm.addEventListener("submit", async function(event) {
    event.preventDefault();

    const isValid = validateLogin();
    if(!isValid){
        return;
    }
    emailError.textContent = "";
    passwordError.textContent ="";


    const loginData = {
        email: emailInput.value.trim(),
        password: passwordInput.value.trim()
    };

    try {
        const response = await logInUser(loginData);
        const data = response.data;

        localStorage.setItem("loggedInUser",JSON.stringify(data));
        localStorage.setItem("token", data.token);

        document.getElementById("message").innerHTML = `
            <div class="alert alert-success">
                ${data.message}
            </div>
        `;

        if(data.role==="ADMIN"){
        
            window.location.href="admin/admin.html"
        }
        else if(data.role==="MEMBER"){
            window.location.href="index.html"
        }
        else{
                throw new Error("Unknown user role");
        }
        loginForm.reset();

    } catch (error) {
        console.log(error.response.data);

        document.getElementById("message").innerHTML = `
            <div class="alert alert-danger">
                ${error.response.data.message}
            </div>
        `;
    }
});

const password = document.getElementById("password");
const eyeIcon = document.getElementById("eyeIcon");

document.getElementById("togglePassword").addEventListener("click", function(){
    if(password.type==="password"){
        password.type="text";
        eyeIcon.classList.remove("bi-eye");
        eyeIcon.classList.add("bi-eye-slash");
    }
    else{
        password.type="password";
        eyeIcon.classList.add("bi-eye");
        eyeIcon.classList.remove("bi-eye-slash");
    }
})