const backendBaseURL = "http://localhost:8081";
// const loggedInUser = JSON.parse(localStorage.getItem("loggedInUser"));
import axios from "https://cdn.jsdelivr.net/npm/axios@1.7.9/+esm";

export const registerUser = async (requestData) =>{
    const response = await axios.post(`${backendBaseURL}/auth/register`,
        requestData
    );
    return response;
};

export const logInUser = async (requestData) =>{
    const response = await axios.post(`${backendBaseURL}/auth/login`,
        requestData
    );
    return response;
};

export const getAllUsers = async (userId) => {
    const response = await axios.get(`${backendBaseURL}/auth/getAllUsers?userId=${userId}`
    );
    return response;
};


export const addProduct = async (userId, requestData) => {
    const response = await axios.post(`${backendBaseURL}/products/add?userId=${userId}`,
        requestData
    );
    return response;
};

export const getAllProducts = async(page,size)=>{
    const response = await axios.get(`${backendBaseURL}/products?page=${page}&size=${size}`
    );
    return response;
};

export const getProductById = async (productId) => {
    const response = await axios.get(`${backendBaseURL}/products/${productId}`
    );
    return response;
};

export const getFilteredProductsByName = async (name, page=0, size=12) =>{
    const response = await axios.get(`${backendBaseURL}/products/filterByName`,
        {
            params:{
                name: name,
                page: page,
                size: size
            }
        }
    );
    return response;
}

export const getFilteredProductsByCategory = async (category, page=0, size=12) =>{
    const response = await axios.get(`${backendBaseURL}/products/filterByCategory`,
        {
            params:{
                category: category,
                page: page,
                size: size
            }
        }
    );
    return response;
}

export const getFilteredProducts = async (name, category, sort, page, size)=>{
    const response = await axios.get(`${backendBaseURL}/products/filterProducts`,
        {
            params:{
                name: name,
                category: category,
                sort: sort,
                page: page,
                size: size
            }
        }
    );
    return response;
}

export const updateProduct = async (userId, productId, requestData) =>{
    const response = await axios.put(`${backendBaseURL}/products/update/${productId}?userId=${userId}`,
        requestData
    );
    return response;
}

export const deleteProduct = async (userId, productId) =>{
    const response = await axios.put(`${backendBaseURL}/products/delete/${productId}?userId=${userId}`);
    return response;
}

export const createOrder = async (cartId, couponId, orderData) => {
    let url = `${backendBaseURL}/orders?cartId=${cartId}`;
    if(couponId){
        url+=`&couponId=${couponId}`
    }
    const response = await axios.post(url,orderData);
    return response;
};

export const getOrderById = async (userId, orderId) => {
    const response = await axios.get(`${backendBaseURL}/orders/${orderId}?userId=${userId}`
    );
    return response;
};

export const getAllOrders = async (userId) => {
    const response = await axios.get(`${backendBaseURL}/orders/getAllOrders?userId=${userId}`
    );
    return response;
};

export const getOrdersByUser = async (userId, targetUserId) => {
    const response = await axios.get(
        `${backendBaseURL}/orders/user/${targetUserId}?userId=${userId}`
    );

    return response;
};

export const myOrderItems = async (orderId, userId) =>{

    console.log("myOrderItems");
    const response = await axios.get(`${backendBaseURL}/orders/${orderId}?userId=${userId}`);
    return response;
}

export const getCart = async (userId)=>{
    const response = await axios.get(`${backendBaseURL}/cart/getCart/${userId}`);
    return response;
}

export const addToCart = async(userId, productId)=>{
    const response = await axios.post(`${backendBaseURL}/cart/addToCart?userId=${userId}&productId=${productId}`);
    return response;
}

export const incrementQuantity = async(userId, productId)=>{
    const response = await axios.put(`${backendBaseURL}/cart/increment?userId=${userId}&productId=${productId}`);
    return response;
}

export const decrementQuantity = async (userId, productId) =>{
    const response = await axios.put(`${backendBaseURL}/cart/decrement?userId=${userId}&productId=${productId}`);
    return response;
}

export const removeCartItem = async (userId, productId) =>{
    const response = await axios.put(`${backendBaseURL}/cart/remove?userId=${userId}&productId=${productId}`);
    return response;
}

export const addToWishlist = async (userId, productId) => {
    const response = await axios.post(
        `${backendBaseURL}/wishlist/addToWishlist?userId=${userId}&productId=${productId}`
    );

    return response;
};

export const getWishlist = async (userId)=>{
    const response = await axios.get(`${backendBaseURL}/wishlist/getWishlist?userId=${userId}`);
    return response;
}

export const removeFromWishlist = async (userId, productId) =>{
    console.log("before requst");
    const response = await axios.put(`${backendBaseURL}/wishlist/removeFromWishlist?productId=${productId}&userId=${userId}`);
    console.log(response);
    return response;
}

export const moveToCart = async (userId, productId) =>{
    const response = await axios.put(`${backendBaseURL}/wishlist/moveToCart?userId=${userId}&productId=${productId}`);
    return response;
}

export const fetchAllCoupons = async () =>{
    const response = await axios.get(`${backendBaseURL}/coupon/fetchAll`);
    return response;
}

export const applyCoupon = async (id, cartId) =>{
    const response = await axios.get(`${backendBaseURL}/coupon/apply/${id}?cartId=${cartId}`);
    return response;
}