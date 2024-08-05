const mongoose = require('mongoose');

const BillSchema = new mongoose.Schema({
    userId: {
        type: String,
        required: true
    },
    status: {
        type: String,
        required: true
    },
    items: [{
        type: String, // Changed to ObjectId
        ref: 'billDetail', 
        required: true
    }],
    totalAmount: {
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


const BillModel = mongoose.model('bill', BillSchema);

module.exports = BillModel;
