const mongoose = require('mongoose');

const CartItemSchema = new mongoose.Schema({
    productId: {
        type: String,
        ref: 'car', 
        required: true
    },
    quantity: {
        type: Number,
        required: true,
        default: 1
    },
    price: {
        type: Number,
        required: true
    },
    name: {
        type: String,
        required: true
    }
});

const CartSchema = new mongoose.Schema({
    userId: {
        type: String,
        required: true
    },
    items: [CartItemSchema],
    totalPrice: {
        type: Number,
        required: true,
        default: 0
    },
    createdAt: {
        type: Date,
        default: Date.now
    },
    updatedAt: {
        type: Date,
        default: Date.now
    }
});

CartSchema.pre('save', function(next) {
    this.totalPrice = this.items.reduce((acc, item) => acc + (item.price * item.quantity), 0);
    this.updatedAt = Date.now();
    next();
});

const CartModel = mongoose.model('cart', CartSchema);

module.exports = CartModel;
