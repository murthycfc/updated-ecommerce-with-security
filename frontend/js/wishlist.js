
import { getWishlist, removeFromWishlist, moveToCart } from "./api.js";

const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));
const wishlistContainer = document.getElementById("wishlistContainer");

if(!loggedInUser){
    window.location.href="http://localhost/login.html"
}

document.getElementById("logoutBtn").addEventListener("click", function(){
    localStorage.clear();
    window.location.href="http://localhost/login.html"
});




let wishlist = null;
document.addEventListener("DOMContentLoaded", async () => {
    console.log("1");
    await loadWishlist();
    console.log("2");
})

async function loadWishlist() {
    try {
        console.log(3);
        const response = await getWishlist(loggedInUser.userId);
        console.log(4)
        wishlist = response.data;
        console.log(wishlist);
        displayWishlist(wishlist);
    } catch (error) {
        console.log(error.message);
    }
}

function displayWishlist(wishlist){
    try {
        if(!wishlist || wishlist.length===0){
            wishlistContainer.innerHTML=`
                <div class="text-center">
                    Your wishlist is empty
                </div>
            `
            return;
        }
        wishlistContainer.innerHTML="";
        wishlist.forEach(product => {
            wishlistContainer.innerHTML+=`
                <div class="col-6 col-lg-4 col-xl-3">
                    <div class="card h-100 shadow-sm">
                        <div class="bg-light" style="height:200px">
                            <button class="btn btn-light position-absolute top-0 end-0 m-3  fs-2"
                                onclick="removeProductFromWishlist('${product.productResponse.id}')"
                                id="wishlist-${product.productResponse.id}"
                                >
                                    ♥
                            </button>

                        </div>
                        <div class="card-body d-flex flex-column">
                            <h5 class="card-text">
                                ${product.productResponse.title}
                            </h5>
    
                            <p class="card-text text-muted">
                                Category: ${product.productResponse.category}
                            </p>

                            <h5 class="text-primary">
                                ₹${product.productResponse.price}
                            </h5>
                            <button class="btn btn-primary mt-auto" onclick="moveProductToCart('${product.productResponse.id}')">
                                Move to cart
                            </button>
                        </div>
                    </div>
                </div>
            `
        });
        
    } catch (error) {
        console.log(error.message);
    }
}


window.moveProductToCart = async function (productId) {
    try {
        const response = await moveToCart(loggedInUser.userId, productId);
        const message = response.data;
        console.log(message);
        loadWishlist();
    } catch (error) {
        console.log(error.message)
    }
}

window.removeProductFromWishlist = async function(productId){
    try {
        const response = await removeFromWishlist(loggedInUser.userId, productId);
        console.log(response);
        document.getElementById(`wishlist-${productId}`).textContent="♡";
        loadWishlist();
    } catch (error) {
        console.log(error);
    }
}