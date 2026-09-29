
import { getAllOrders } from "../js/api.js";
import { addProduct } from "../js/api.js";
import { updateProduct } from "../js/api.js";
import { getProductById } from "../js/api.js";
import { getAllProducts } from "../js/api.js";
import { deleteProduct } from "../js/api.js";
import { getOrdersByUser } from "../js/api.js";
import { getAllUsers } from "../js/api.js";
import { myOrderItems } from "../js/api.js";


const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));

const addProductBtn = document.getElementById("addProductBtn");
const addProductModalElement = document.getElementById("addProductModal");
const addProductModal = new bootstrap.Modal(addProductModalElement);

const editProductForm = document.getElementById("editProductForm");


if(!loggedInUser){
    window.location.href="../login.html"
}
else if(loggedInUser.role !== "ADMIN") {
    window.location.href = "../index.html";
}

document.getElementById("adminName").textContent=loggedInUser.name;

document.getElementById("logoutBtn").addEventListener("click", function(){
    localStorage.removeItem("loggedInUser");
    window.location.href="../login.html";
})

function showSection(sectionId){
    const sections = [
        "dashboard",
        "products",
        "orders",
        "users"
    ];
    
    sections.forEach(function(section){
        document.getElementById(section).classList.add("d-none");
    });
    document.getElementById(sectionId).classList.remove("d-none");
}
window.showSection = showSection;


//Products
async function loadProducts() {
    const tableBody = document.getElementById("productTableBody");
    try {
        const response = await getAllProducts();
        
        const products =  response.data;
        tableBody.innerHTML="";
        products.forEach(function(product){
            tableBody.innerHTML+= `
                <tr class="">
                    
                    <td>${product.title}</td>
                    <td>${product.category}</td>
                    <td>₹${product.price}</td>
                    <td class="d-flex justify-content-evenly">
                        <button class="btn btn-warning " onclick="editProduct('${product.id}')">
                            Edit
                        </button>
                        <button class="btn btn-danger " onclick="removeProduct('${product.id}')">
                            Delete
                        </button>
                    </td>
                </tr>
            `
        });
        document.getElementById("totalProducts").textContent=products.length;
    } catch (error) {
        console.error("Error loading products:", error);
        tableBody.innerHTML= `
            <tr>
                <td colspan="5" class="text-center text-danger">
                    Failed to load products
                </td>
            </tr>
        `
    }
}

addProductBtn.addEventListener("click", function(){
    document.getElementById("addProductForm").reset();
    document.getElementById("productMessage").innerHTML = "";
    addProductModal.show();
})

const addProductForm = document.getElementById("addProductForm");
addProductForm.addEventListener("submit", async function(event){
    event.preventDefault();
    const productData ={
        title: document.getElementById("productTitle").value,
        category: document.getElementById("productCategory").value,
        description: document.getElementById("productDescription").value,
        price: Number(document.getElementById("productPrice").value)
    };

    try {
        const response = await addProduct(loggedInUser.userId, productData);
            
        const data = await response.data;
        
        document.getElementById("productMessage").innerHTML=`
            <div class="alert alert-success">
                Product added successfulyy
            </div>
        `;
        addProductForm.reset();
        loadProducts();
        setTimeout(function(){
            addProductModal.hide();
        },1000)
    } catch (error) {
        document.getElementById("productMessage").innerHTML= `
            <div class="alert alert-danger">
                ${error.message}
            </div>
        `;
    }
})

async function editProduct(productId) {
    try {
        const response = await getProductById(productId);
        
        const product = await response.data;

        console.log(product);

        document.getElementById("editProductId").value = product.id;
        document.getElementById("editTitle").value = product.title;
        document.getElementById("editCategory").value = product.category;
        document.getElementById("editPrice").value = product.price;
        document.getElementById("editDescription").value = product.description || "";

        const editProductModalElement = document.getElementById("editProductModal");
        const editProductModel = new bootstrap.Modal(editProductModalElement);
        editProductModel.show();
    } catch (error) {
        alert(error.message);
    }   
}

if(editProductForm){
    editProductForm.addEventListener("submit", async function(event){
        event.preventDefault();
         const productId = document.getElementById("editProductId").value;
         const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));

         const updatedProduct = {
            title: document.getElementById("editTitle").value,
            category:document.getElementById("editCategory").value,
            price: Number(document.getElementById("editPrice").value),
            description: document.getElementById("editDescription").value
         };

         try {
            const response = await updateProduct( loggedInUser.userId, productId,updatedProduct);
        
            const data = await response.data;
            
            alert("Product updated successfully");
            const modalElement = document.getElementById("editProductModal");
            const modal = bootstrap.Modal.getInstance(modalElement);
            modal.hide();
            loadProducts();

         } catch (error) {
            alert(error.message);
         }
    })
}

async function removeProduct(productId){
    const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));
    console.log(`Deleting:  ${productId}`)
    if (!loggedInUser){
        alert("User not logged in");
        return;
    }
    const confirmed = confirm("Are you sure you want to delete this product?");
    if(!confirmed){
        return;
    }
    try {
        const response = await deleteProduct(loggedInUser.userId,productId )
            
        alert("Product deleted successfully");
        loadProducts();
    } catch (error) {
        alert(error.message);
    }
}
window.removeProduct = removeProduct;
window.editProduct = editProduct;
loadProducts();


// Orders
async function loadOrders() {
    const orderBody = document.getElementById("orderTableBody");
    try {
        const response = await getAllOrders(loggedInUser.userId);
    
        const orders = await response.data;
        orderBody.innerHTML="";
        orders.forEach(function(order){
            orderBody.innerHTML+= `
                <tr>
                    <td>${order.orderNumber}</td>
                    <td>
                        ${order.user.name}
                        <br>
                        <small class="text-muted">
                            ${order.user.email}
                        </small>
                    </td>
                    <td>${order.subTotal}</td>
                    <td>${order.discountApplied}</td>
                    <td>${order.finalTotal}</td>
                    <td>${order.orderedDate.split('T')[0]}</td>
                    <td>${order.deliveryDate}</td>
                    <td>
                        <button class="btn btn-primary btn-sm" onclick="viewOrder('${order.id}')">
                            View
                        </button>
                    </td>
                </tr>
            `;
        })
        document.getElementById("totalOrders").textContent=orders.length;
    } catch (error) {
        orderBody.innerHTML=`
            <tr>
                <td colspan="6" class="text-center text-danger">
                    Failed to load orders
                </td>
            </tr>
        `
    }   
}

async function viewOrder(orderId) {
    try {
        const response = await myOrderItems(orderId, loggedInUser.userId);

        const order = response.data;

        console.log(order);
        document.getElementById("viewOrderId").textContent=order.orderNumber;
        document.getElementById("viewOrderCustomer").textContent = order.user.name;
        document.getElementById("viewOrderEmail").textContent = order.user.email;
        document.getElementById("viewOrderPlace").textContent = order.place;

        const orderItemsContainer = document.getElementById("viewOrderItems");
        orderItemsContainer .innerHTML="";

        order.orderItems.forEach(function(item){
            const productTotal = item.price * item.quantity;
            orderItemsContainer.innerHTML+=`
                <tr>
                    <td>${item.product.title}</td>
                    <td>${item.product.category}</td>
                    <td>${item.price}</td>
                    <td>${item.quantity}</td>
                    <td>${productTotal}</td>
                </tr>
            `
        });

        const orderItemsModalElement = document.getElementById("viewOrderModal");
        const orderItemsModal = new bootstrap.Modal(orderItemsModalElement);
        orderItemsModal.show();
    } catch (error) {
        // console.log(error);
        alert(error.message);
    }
}
window.viewOrder = viewOrder ;

loadOrders();



// Users
async function loadUsers() {
    const userBody = document.getElementById("userTableBody");
    try {
        const response = await getAllUsers (loggedInUser.userId);
        
        // console.log(response);
        const users = response.data;
        
        userBody.innerHTML="";
        users.forEach(function(user){
            userBody.innerHTML+=`
                <tr>
                    
                    <td>${user.name}</td>
                    <td>${user.email}</td>
                    <td>${user.role}</td>
                    <td>
                        <button class="btn btn-primary btn-sm" onclick="viewUserOrders('${user.id}', '${user.email}')">
                            View Orders
                        </button>
                    </td>
                </tr>
            `
        })
        document.getElementById("totalUsers").textContent = users.length;
    } catch (error) {
        console.log(error);
        userBody.innerHTML=`
            <tr>
                <td colspan="4" class = "text-center text-danger">
                    Failed to load orders
                </td>
            </tr>
        `
    }
} 

async function viewUserOrders(targetUserId, email) {
    try {
        const response = await getOrdersByUser(loggedInUser.userId,targetUserId);
        // if (!response.ok) {
        //     const data = await response.data;
        //     throw new Error(data.message || "Failed to load user orders");
        // }

        const orders = await response.data;
        console.log(orders);
        document.getElementById("selectedUserEmail").textContent = email;
        const orderBody = document.getElementById("userOrdersTableBody");
        orderBody.innerHTML = "";

        if (orders.length === 0) {
            orderBody.innerHTML = `
                <tr>
                    <td colspan="6"
                        class="text-center text-muted">
                        This user has no orders.
                    </td>
                </tr>
            `;
        } 
        else {
            orders.forEach(function(order){
                orderBody.innerHTML+=`
                    <tr>
                        <td>${order.orderNumber}</td>
                        <td>₹${order.subTotal}</td>
                        <td>₹${order.discountApplied}</td>
                        <td>₹${order.finalTotal}</td>
                        <td>${order.orderedDate.split('T')[0]}</td>
                        <td>${order.deliveryDate}</td>
                    </tr>
                `
            });
        }

        const modalElement = document.getElementById("userOrdersModal");
        const modal = new bootstrap.Modal(modalElement);
        modal.show();


    } catch (error) {
        // console.error(error);
        alert(error.message);
    }
}
window.viewUserOrders = viewUserOrders ;
loadUsers();


