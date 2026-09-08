import {registerUser} from "./api.js";

const registerForm = document.getElementById("registerForm");
const nameError = document.getElementById("nameError");
const emailError = document.getElementById("emailError");
const passwordError = document.getElementById("passwordError");
const phoneNumberError = document.getElementById("phoneNumberError");

const nameInput = document.getElementById("name");
const emailInput = document.getElementById("email");
const passwordInput = document.getElementById("password");
const phoneNumberInput = document.getElementById("phoneNumber");


function validateRegister(){
    let isValid = true;

    nameError.textContent = "";
    emailError.textContent = "";
    passwordError.textContent ="";
    phoneNumberError.textContent = "";

    const name =  nameInput.value.trim()
    if(name===""){
        nameError.textContent="Name is required";
        isValid = false;
    }
    else if(name.length<3){
        nameError.textContent = "Name must contain at least 3 characters";
        isValid = false;
    }
    else if(!/^[A-Za-z\s]+$/.test(name)){
        nameError.textContent = "Name can contain only letters";
        isValid = false;
    }

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

    const phone = phoneNumberInput.value.trim();
    if(phone===""){
        phoneNumberError.textContent="Phone number is required";
        isValid = false;
    }
    else if(!/^[6-9]\d{9}$/.test(phone)){
        phoneNumberError.textContent = "Enter a valid 10 digit phone number";
        isValid = false;
    }

    return isValid;
}


registerForm.addEventListener("submit", async function(event){
    event.preventDefault();
    const isValid = validateRegister();
    if(!isValid){
        return;
    }
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const phoneNumber = document.getElementById("phoneNumber").value;
    const password = document.getElementById("password").value;

    const registerData ={
        name: name,
        email: email,
        phoneNumber: phoneNumber,
        password: password
    };

    try {
        const response = await registerUser(registerData);
        // console.log(response);
        const data = await response.data;
        // console.log(data);

        localStorage.setItem("loggedInUser",JSON.stringify(data));

        if(data.role==="ADMIN"){
            window.location.href="admin/admin.html"
        }
        else if(data.role==="MEMBER"){
            window.location.href="index.html"
        }
        else{
            throw new Error("Unknown user role");
        }

        document.getElementById("message").innerHTML=`
            <div class="alert alert-success">
                ${data.message}
            </div>
        `;
        registerForm.reset();

    } catch (error) {
        document.getElementById("message").innerHTML=`
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