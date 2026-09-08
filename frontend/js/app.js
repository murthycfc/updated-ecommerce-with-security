import {getAllProducts, incrementQuantity, decrementQuantity, getWishlist, getFilteredProducts} from "./api.js"
import { getOrdersByUser } from "./api.js";
import {myOrderItems} from "./api.js"
import { addToCart, getCart, addToWishlist, removeCartItem } from "./api.js";


const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));
if(!loggedInUser){
    window.location.href="http://localhost/ecommerce-shopping-cart/login.html"
}

document.getElementById("logoutBtn").addEventListener("click", function(){
    localStorage.clear();
    window.location.href="http://localhost/login.html"
});

const productContainer = document.getElementById("productContainer");
const searchInput = document.getElementById("searchInput");
const categoryFilter = document.getElementById("categoryFilter");
const cartContainer = document.getElementById("cartContainer");
const cartCount = document.getElementById("cartCount");
const paginationContainer = document.getElementById("paginationContainer");


let products = [];
let cart = null;
let currentPage = 0;
const productsPerPage = 12;

let currentFilter = "All";


document.getElementById("cartLink").addEventListener("click", function(){
    window.location.href = "./cart.html";
});

document.getElementById("wishlistbtn").addEventListener("click", function(){
    window.location.href="./wishlist.html";
})

document.addEventListener("DOMContentLoaded", async ()=>{
    await loadCart();
    await fetchProducts();
    await loadWishlist();
})

async function loadCart() {
    try {
        const response = await getCart(loggedInUser.userId);
        cart = response.data;
        updateCartCount();
    } catch (error) {
        console.log(error.message);
        cart = null;
    }
}
async function fetchProducts(page=0) {
    try {
        // const response = await getAllProducts(page, productsPerPage);
        
        const searchText = searchInput.value.trim();
        const selectedCategory = categoryFilter.value;
        const sort = sortFilter.value;

        const response = await getFilteredProducts(searchText, selectedCategory,sort, page, productsPerPage );
        
        products = response.data;
        currentPage = products.number;
        console.log(currentPage)

        console.log(products);

        displayProducts(products);
        
    } catch (error) {
        console.log(error.message);
        productContainer.innerHTML= `
            <div class="col-12 text-center">
                <h4 class="text-danger">
                    Failed to load products
                </h4>
            </div>
        `;
    }
}
async function loadWishlist() {
    try {
        const response = await getWishlist(loggedInUser.userId);
        const data = response.data;
        console.log("wishlist data",data);
        updateWishlistCount(data)
    } catch (error) {
        console.log(error.message)
    }
    
}

function updateWishlistCount(data){
    document.getElementById("wishlistCount").textContent=data.length;
}

function displayProducts(productList) {
    productContainer.innerHTML="";

    if(productList.content.length===0){
        productContainer.innerHTML=`
            <div class="col-12 text-center">
                <h4 class="text-muted">
                    No products found
                </h4>
            </div>
        `;
        paginationContainer.innerHTML = "";
        return;
    }
    
    productList.content.forEach(function(product){
        const cartItem = cart?.cartItems?.find(function(item){
            return item.product.id === product.id
            
        });
    let cartButton;

    if(cartItem){
        cartButton=`
            <div class="d-flex justify-content-center align-items-center mt-auto">
                <button class="btn btn-outline-secondary" onclick="decreaseQuantity('${product.id}')">
                    -
                </button>

                <span class="mx-3">${cartItem.quantity}</span>

                <button class="btn btn-outline-secondary" onclick="increaseQuantity('${product.id}')">
                    +
                </button>
            </div>
        `}
        else{
            cartButton = `
                <button class="btn btn-primary mt-auto" onclick="addProductToCart('${product.id}')">
                    Add to Cart
                </button>
            `
        }

        productContainer.innerHTML+= `
        <div class="col-6 col-lg-4 col-xl-3">
            <div class="card h-100 shadow-sm">
                <button class="btn btn-light position-absolute top-0 end-0 m-3  fs-2"
                onclick="addProductToWishlist('${product.id}')"
                id="wishlist-${product.id}"
                >
                    ♡
                </button>
                <div
                    class="bg-light"
                    style="height:200px;"
                ></div>
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">
                       ${product.title}
                    </h5>

                    <p class="card-text text-muted">
                         Category: ${product.category}
                    </p>

                    <h5 class="text-primary">
                        ₹${product.price}
                    </h5>

                    ${cartButton}
                </div>
            </div>
        </div>
        `
    })
    displayPagination(productList);
}

function displayPagination(productList){
    paginationContainer.innerHTML = "";
    const totalPages = productList.totalPages;
    if (totalPages <= 1) {
        return;
    }

    const previousButton = document.createElement("button");
    previousButton.textContent = "Previous";
    previousButton.className = "btn btn-outline-primary mx-1";
    previousButton.disabled = productList.first;

    previousButton.addEventListener("click",function(){
        if(!productList.first){
            currentPage--;
            fetchProducts(currentPage);
        }
    });
    paginationContainer.appendChild(previousButton);

    for(let page=0; page<totalPages; page++){
        const pageButton = document.createElement("button");
        pageButton.textContent=page+1;
        pageButton.className= "btn btn-outline-primary mx-1";
        if(page===productList.number){
            pageButton.classList.add("active");
        }
        pageButton.addEventListener("click", function(){
            currentPage = page;
            fetchProducts(currentPage);
        });
        paginationContainer.appendChild(pageButton);
    }

    const nextButton = document.createElement("button");
    nextButton.textContent = "Next";
    nextButton.className = "btn btn-outline-primary mx-1";
    nextButton.disabled = productList.last;

    nextButton.addEventListener("click", function() {
        if (!productList.last) {
            currentPage++;
            fetchProducts(currentPage);
        }
    });
    paginationContainer.appendChild(nextButton);
}

window.addProductToCart = async function ( productId) {
    try {
        await addToCart(loggedInUser.userId, productId);
        await loadCart();
        displayProducts(products);
    } catch (error) {
        console.log(error.message);
    }
};

window.increaseQuantity = async function (productId){
    try {
        await incrementQuantity(loggedInUser.userId, productId);
        await loadCart();
        displayProducts(products);
    } catch (error) {
        console.log(error.message);
        alert(error.message);
    }
}

window.decreaseQuantity = async function (productId){
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
            displayProducts(products);
            return;
        }
        await decrementQuantity(loggedInUser.userId, productId);
        await loadCart();
        displayProducts(products);
    } catch (error) {
        console.log(error.message);
        alert(error.message);
    }
}


function updateCartCount(){
    let totalQuantity = 0;
    cart?.cartItems?.forEach(function(item){
        totalQuantity+=item.quantity;
    });
    cartCount.textContent=totalQuantity;
}

function debounce(callback, delay){
    let timer;
    
    return function(){
        clearTimeout(timer);
        timer = setTimeout(function(){
            callback();
        },delay);
    };
}

function applyFilters(){
    console.log("Fetching....");
    // searchText = searchInput.value.toLowerCase();
    // selectedCategory = categoryFilter.value;

    currentPage = 0;
    fetchProducts(currentPage);
}

const debouncedSearch = debounce(applyFilters,500);

searchInput.addEventListener("input",debouncedSearch);

categoryFilter.addEventListener("change", function(){
    applyFilters();
});

sortFilter.addEventListener("change", function(){
    applyFilters();
})



document.getElementById("userOrders").addEventListener("click", async function(){
    const myOrdersTableBody = document.getElementById("myOrdersTableBody");

    try {
        const response = await getOrdersByUser(loggedInUser.userId, loggedInUser.userId);
       
        const orders = await response.data;

        myOrdersTableBody.innerHTML = "";
        if (orders.length === 0) {
            myOrdersTableBody.innerHTML = `
                <tr>
                    <td colspan="7" class="text-center text-muted">
                        You have not placed any orders yet.
                    </td>
                </tr>
            `;
        }
        else {
            orders.forEach(function (order){
                myOrdersTableBody.innerHTML+=`
                    <tr>
                        <td>${order.orderNumber}</td>
                        <td>₹${order.subTotal}</td>
                        <td>${order.discountApplied}</td>
                        <td>₹${order.finalTotal}</td>
                        <td>${order.orderedDate.split('T')[0]}</td>
                        <td>${order.deliveryDate}</td>
                        <td>
                            <button class="btn btn-primary btn-sm" onclick="viewMyOrderItems('${order.id}')">
                                View Products
                            </button>
                        </td>
                    </tr>
                `
            });
        }
        const viewOrderModalElement = document.getElementById("viewOrderModal");
        const viewOrderModal = new bootstrap.Modal(viewOrderModalElement);
        viewOrderModal.show();

    } catch (error) {
        console.error(error);
        alert(error.message);
    }

})

async function viewMyOrderItems(orderId) {
    try {
        const response = await myOrderItems(orderId, loggedInUser.userId);
        console.log("response", response);
        const order = await response.data;
        console.log("Order",order);

        document.getElementById("selectedOrderId").textContent=order.orderNumber;
        const orderItemsTableBody = document.getElementById("orderItemsTableBody");
        orderItemsTableBody.innerHTML="";

        order.orderItems.forEach(function (item){
            const total = item.price*item.quantity;
            orderItemsTableBody.innerHTML+=`
                <tr>
                    <td>${item.product.title}</td>
                    <td>${item.product.category}</td>
                    <td>${item.price}</td>
                    <td>${item.quantity}</td>
                    <td>${total}</td>
                </tr>
            `
        });

        const orderItemsModalElement = document.getElementById("orderItemsModal");
        const orderItemsModal = new bootstrap.Modal(orderItemsModalElement);
        orderItemsModal.show();
    } catch (error) {
        console.error(error);
        alert(error.message);
    }    
}
window.viewMyOrderItems = viewMyOrderItems;


window.addProductToWishlist = async function (productId) {
    try {
        await addToWishlist(loggedInUser.userId, productId);
        const wishlistBtn = document.getElementById(`wishlist-${productId}`);
        wishlistBtn.textContent = "♥";
        loadWishlist();
    } catch (error) {
        console.log(error.message);
    }
}


