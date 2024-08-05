const mongoose = require('mongoose');

const BillDetailSchema = new mongoose.Schema({
    billId:{
        type: String,
        ref: 'bill',
        required: true
    },
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
    total: {
        type: Number,
        required: true
    },
    name: {
        type: String,
        required: true
    }
});

BillDetailSchema.pre('save', function(next) {
    this.total = this.quantity * this.price;
    next();
});

const BillDetailModel = mongoose.model('billDetail', BillDetailSchema);

module.exports = BillDetailModel;