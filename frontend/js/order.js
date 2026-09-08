import {getCart, incrementQuantity, decrementQuantity, removeCartItem, createOrder, fetchAllCoupons, applyCoupon} from "./api.js"

let cart = [];

const orderCartContainer = document.getElementById("orderCartContainer");
const orderSubtotal = document.getElementById("orderSubtotal");
const orderDiscountPercent = document.getElementById("orderDiscountPercent");
const orderDiscountAmount = document.getElementById("orderDiscountAmount");
const orderFinalTotal = document.getElementById("orderFinalTotal");
const checkoutForm = document.getElementById("checkoutForm");
const orderPage = document.getElementById("orderPage");
const goToShop = document.getElementById("goToShop");
const couponContainer = document.getElementById("couponContainer")


const loggedInUser = JSON.parse(
    localStorage.getItem("loggedInUser")
);

if(!loggedInUser) {
    alert("Please login first.");
    window.location.href = "login.html";
}
else{
    document.getElementById("customerName").value =
        loggedInUser.name;

    document.getElementById("customerEmail").value =
        loggedInUser.email;
}

document.addEventListener("DOMContentLoaded", ()=>{
    loadCart();
    loadCoupons();
})

async function loadCart() {
    try {
        const response = await getCart(loggedInUser.userId);
        cart = response.data;
        console.log(cart);
        displayOrderCart(cart);
        displayBill(cart);
    } catch (error) {
        console.log(error);
        orderCartContainer.innerHTML = `
            <div class="col-12 text-danger">
                Failed to load cart
            </div>
        `
    }
}

function displayOrderCart(){
    orderCartContainer.innerHTML="";
    if(!cart || !cart.cartItems){
        orderCartContainer.innerHTML= `
            <div class="text-danger">
                Failed to display cart
            </div>
        `;
        return ;
    }
    if(cart.cartItems.length==0){
        orderCartContainer.innerHTML = `
            <div class="text-danger">
                Your cart is empty
            </div>
        `
    }
    cart.cartItems.forEach(function(item){
        const product = item.product;
        orderCartContainer.innerHTML+=`
            <div class="card mb-3">
                <div class="card-body">
                    <div class="row align-items-center">
                        <div class="col-md-3">
                            <h5 class="mb-1">
                                ${product.title}
                            </h5>
                            <p class="text-muted mb-0">
                                ${product.category}
                            </p>
                        </div>
                        <div class="col-md-2">
                            <strong>
                                ₹${product.price}
                            </strong>
                        </div>
                        <div class="col-md-3">
                            <button class="btn btn-outline-secondary" onclick="decreaseQuantity('${product.id}')">
                                    -
                                </button>
                                <span class="mx-3">
                                    ${item.quantity}
                                </span>
                                <button class="btn btn-outline-secondary" onclick="increaseQuantity('${product.id}')">
                                    +
                            </button>
                        </div>
                        <div class="col-md-2">
                            <strong>
                                ₹${item.totalPrice}
                            </strong>
                        </div>
                        <div class="col-md-2">
                            <button class="btn btn-danger" onclick="removeFromCart('${product.id}')">
                                Remove
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `
    });
}

function displayBill(cart){
    orderSubtotal.textContent = `₹${Number(cart.subTotal).toFixed(2)}`;
    orderFinalTotal.textContent = `₹${Number(cart.finalTotal).toFixed(2)}`;
}
function displayUpdateBill(data){
    orderDiscountAmount.textContent = `₹${data.appliedDiscount}`;
    const subTotal = orderSubtotal.textContent.replace(/[^0-9.]/g,'');
    const discount = orderDiscountAmount.textContent.replace(/[^0-9.]/g, '');
    const finalTotal = subTotal - discount;

    orderFinalTotal.textContent = `₹${finalTotal.toFixed(2)}`;
}
window.increaseQuantity = async function (productId) {
    try {
        await incrementQuantity(loggedInUser.userId, productId);
        await loadCart();
        appliedCoupon=null;
        orderDiscountAmount.textContent=`₹${0}`
        document.querySelectorAll("[id^='couponButton']").forEach(button =>{
            button.textContent = "Apply Coupon";
            button.disabled = false;
        })

        document.querySelectorAll("[id^='removeButton']").forEach(button =>{
            button.disabled = true;
        })

        document.querySelectorAll("[id^='couponStatus']").forEach(status =>{
            status.textContent = "";
        })

        document.querySelectorAll("[id^='couponBody']").forEach(body=>{
             body.classList.remove("bg-success-subtle");
        })


    } catch (error) {
        console.log(error.message);
    }
}

window.decreaseQuantity = async function (productId) {
    try {
        await decrementQuantity(loggedInUser.userId, productId);
        await loadCart();
        appliedCoupon=null;
        orderDiscountAmount.textContent=`₹${0}`
        document.querySelectorAll("[id^='couponButton']").forEach(button =>{
            button.textContent = "Apply Coupon";
            button.disabled = false;
        })

        document.querySelectorAll("[id^='removeButton']").forEach(button =>{
            button.disabled = true;
        })

        document.querySelectorAll("[id^='couponStatus']").forEach(status =>{
            status.textContent = "";
        })

        document.querySelectorAll("[id^='couponBody']").forEach(body=>{
             body.classList.remove("bg-success-subtle");
        })

    } catch (error) {
        console.log(error.message);
    }
}

window.removeFromCart = async function (productId) {
    try {
        await removeCartItem(loggedInUser.userId, productId);
        await loadCart();
        appliedCoupon=null;
        orderDiscountAmount.textContent=`₹${0}`
        document.querySelectorAll("[id^='couponButton']").forEach(button =>{
            button.textContent = "Apply Coupon";
            button.disabled = false;
        })

        document.querySelectorAll("[id^='removeButton']").forEach(button =>{
            button.disabled = true;
        })

        document.querySelectorAll("[id^='couponStatus']").forEach(status =>{
            status.textContent = "";
        })

        document.querySelectorAll("[id^='couponBody']").forEach(body=>{
             body.classList.remove("bg-success-subtle");
        })
    } catch (error) {
        console.log(error.message);
    }
}


async function loadCoupons(){
    try {
        const response = await fetchAllCoupons();
        const data = response.data;
        displayCoupons(data);
    } catch (error) {
        couponContainer.innerHTML=`
            <div class="text-danger">
                Failed to load coupons
            </div>
        `
    }
}
function displayCoupons(data){
    couponContainer.innerHTML="";
    if(data.length===0){
        couponContainer.innerHTML=`
            <div class="text-warning">
                No coupons available.
            </div>
        `
    }
    data.forEach((coupon)=>{
        const couponCard = `
            <div class="col-lg-6 col-lg-4">
                <div class="card shadow-sm h-100 border-success">
                    <div id="couponBody${coupon.id}" class="card-body">
                        <div  class="d-flex justify-content-between align-items-center mb-3">
                            <div class="col-4">
                                <h5 class="card-title mb-0 text-primary">
                                ${coupon.name}
                                </h5>
                                <p id="couponStatus${coupon.id}"></p>
                            </div>
                            
                            <button id="couponButton${coupon.id}" class="col-4 btn btn-success w-10" 
                                onclick="applyCoupon('${coupon.id}')">
                                Apply Coupon
                            </button>

                            <button id="removeButton${coupon.id}" class="col-3 btn btn-danger w-10"
                                onclick="removeCoupon('${coupon.id}')" disabled>
                                Remove
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `
        couponContainer.innerHTML+=couponCard;
    })
}

let appliedCoupon=null;

window.applyCoupon = async function (id) {
    try {
        const response = await getCart(loggedInUser.userId);
        cart = response.data;
        console.log(cart.cartItems);
        const result = await applyCoupon(id, cart.id);

        document.querySelectorAll("[id^='couponButton']").forEach(button =>{
            button.textContent = "Apply Coupon";
            button.disabled = false;
        })
        document.querySelectorAll("[id^='removeButton']").forEach(button=>{
            button.disabled = true;
        })

        document.querySelectorAll("[id^='couponStatus']").forEach(status => {
            status.textContent="";
        });

        document.querySelectorAll("[id^='couponBody']").forEach(body=>{
             body.classList.remove("bg-success-subtle");
        })

        appliedCoupon=id;
        console.log(result.data);
        displayUpdateBill(result.data);
        const selectedButton = document.getElementById(`couponButton${id}`);
        selectedButton.textContent = "Applied";
        selectedButton.disabled = true;
        document.getElementById(`couponStatus${id}`).textContent = "Coupon Applied";
        document.getElementById(`removeButton${id}`).disabled = false;
        document.getElementById(`couponBody${id}`).classList.add("bg-success-subtle");;
        console.log(result.subTotal)
        console.log(result);
    } catch (error) {
        // alert(error.response.data.message)
        // document.querySelectorAll("[id^='couponStatus']").forEach(status =>{
        //     status.textContent = "";
        // })
        document.getElementById(`couponStatus${id}`).textContent = error.response.data.message;
    }
}

window.removeCoupon = function(id){
    
        const removeButton = document.getElementById(`couponButton${id}`);
        removeButton.textContent = "Apply Coupon";
        removeButton.disabled = true;
        document.getElementById(`couponButton${id}`).disabled = false;
        document.getElementById(`couponStatus${id}`).textContent = ""
        document.getElementById(`removeButton${id}`).disabled = true;
        document.getElementById(`couponBody${id}`).classList.remove("bg-success-subtle");
        appliedCoupon = null;
        orderDiscountAmount.textContent=`₹${0}`
}

checkoutForm.addEventListener("submit", function(event){
    event.preventDefault();
    if(!checkoutForm.checkValidity()){
        checkoutForm.classList.add("was-validated");
        return;
    }
    confirmOrder();
})

async function confirmOrder(){
    if (!cart || !cart.cartItems || cart.cartItems.length === 0){
        alert("Your cart is empty. Please add products before confirming the order.");
        return;
    }

    const loggedInUser = JSON.parse(
        localStorage.getItem("loggedInUser")
    );

    const customerName = document.getElementById("customerName").value;
    const customerEmail = document.getElementById("customerEmail").value;

    const orderData = {
        
        place: document.getElementById("customerAddress").value,
        deliveryDate: getDeliveryDateForBackend()
    };
    console.log("Order request:", orderData);

    try {
        const cartResponse = await getCart(loggedInUser.userId);
        let cart = cartResponse.data;
        const response = await createOrder(cart.id, appliedCoupon, orderData);
        const data = response.data;
        console.log("ordersResponse" ,response);
        
        let orderedItems = cart.cartItems;
        
        console.log("order items: ", orderedItems);

        for(const item of orderedItems){
            await removeCartItem(loggedInUser.userId, item.product.id);

        }
        console.log("Cart is cleared "+ orderedItems);


        displaySuccessMessage(customerName, customerEmail,  "ORD-" + data.orderNumber,data.orderedDate.split("T")[0], data.deliveryDate);
        
        cart=null;
        console.log(cart);

        setTimeout(()=>{
            window.location.href="./index.html";
        }, 5000)

    } catch (error) {
        console.error("Order creation failed:", error);
        alert(error.response.data.message);
    }
}

function getDeliveryDateForBackend(){
    const today = new Date();
    today.setDate(today.getDate() + 5);
    return today.toISOString().split("T")[0];
}

function displaySuccessMessage(customerName, customerEmail, orderNumber, orderDate, deliveryDate){
    orderPage.innerHTML = `
    <div class="d-flex justify-content-center align-items-center">
        <div
            class="card shadow-sm text-center p-3"
            style="max-width: 600px; width: 100%;"
        >
            <div class="mb-4">
                <div
                    class="rounded-circle bg-success text-white d-flex justify-content-center align-items-center mx-auto"
                    style="width: 70px; height: 70px; font-size: 40px;"
                >
                    ✓
                </div>
            </div>
            <h2 class="text-success mb-2">
                Order Confirmed!
            </h2>

            <p class="lead mb-2">
                Thank you,
                <strong>${customerName}</strong>!
            </p>

            <p class="mb-2">
                Your order has been successfully placed.
            </p>

            <hr class="mb-4">

            <p class="mb-2">
                Order ID
            </p>
            <h5 class="text-primary mb-2">
                ${orderNumber}
            </h5>

            <p class="mb-2">
                Order Date
            </p>
            <h5 class="text-primary mb-2">
                ${orderDate}
            </h5>

            <p class="mb-2">
                Confirmation has been sent to:
            </p>
            <h5 class="mb-2">
                ${customerEmail}
            </h5>

            <p class="mb-2">
                Expected Delivery Date:
            </p>

            <h5 class="text-primary mb-2">
                ${deliveryDate}
            </h5>

            <a
                href="index.html"
                class="btn btn-primary px-4 py-2"
            >
                Continue Shopping
            </a>

        </div>

    </div>

`;
}