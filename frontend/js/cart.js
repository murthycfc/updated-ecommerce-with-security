import { getCart, incrementQuantity, decrementQuantity, removeCartItem } from "./api.js";

const cartContainer = document.getElementById("cartContainer");
const cartItems = document.getElementById("cartItems");
const emptyCart = document.getElementById("emptyCart");

const subTotal = document.getElementById("subtotal");
const discountPercent = document.getElementById("discountPercent");
const discountAmount = document.getElementById("discountAmount");
const finalTotal = document.getElementById("finalTotal");
const orderProceedBtn = document.getElementById("orderProceed");

const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));
let cart = null;

if(!loggedInUser){
    window.location.href="http://localhost/login.html"
}

document.getElementById("logoutBtn").addEventListener("click", function(){
    localStorage.clear();
    window.location.href="http://localhost/login.html"
});

document.addEventListener("DOMContentLoaded", ()=>{
    loadCart();
});



async function loadCart() {
    try {
        const response = await getCart(loggedInUser.userId);
        cart = response.data;
        console.log("Cart:",cart); 
        displayCart(cart);

    } catch (error) {
        console.error("Error loading cart:", error.message);
    }
}

function displayCart(cart){
    cartItems.innerHTML="";
    if(!cart || !cart.cartItems || cart.cartItems.length==0){
        cartContainer.classList.add("d-none");
        emptyCart.classList.remove("d-none");
        return ;
    }
    cartContainer.classList.remove("d-none");
    emptyCart.classList.add("d-none");
    
    cart.cartItems.forEach(item =>{
        const product = item.product;
        cartItems.innerHTML+=`
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
    subTotal.textContent = `₹${Number(cart.subTotal).toFixed(2)}`;
    finalTotal.textContent = `₹${Number(cart.finalTotal).toFixed(2)}`;
}

window.increaseQuantity = async function (productId) {
    try {
        await incrementQuantity(loggedInUser.userId, productId);
        await loadCart();
    } catch (error) {
        console.log(error.message);
    }
}

window.decreaseQuantity = async function (productId) {
    try {
        const cartItem = cart?.cartItems?.find(function(item){
            return item.product.id === productId;
        })
        if(!cartItem){
            return;
        }
        if(cartItem.quantity<=1){
            await removeCartItem(loggedInUser.userId, productId);
            await loadCart();
        }
        await decrementQuantity(loggedInUser.userId, productId);
        await loadCart();
    } catch (error) {
        console.log(error.message);
    }
}

window.removeFromCart = async function (productId) {
    try {
        await removeCartItem(loggedInUser.userId, productId);
        await loadCart();
    } catch (error) {
        console.log(error.message);
    }
}

orderProceedBtn.addEventListener("click", function(){
    if (cart && cart.cartItems && cart.cartItems.length > 0) {
        window.location.href =
            "http://localhost/order.html";
    } else {
        alert("Your cart is empty");
    }
})


