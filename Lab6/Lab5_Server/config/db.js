const mongoose = require('mongoose');

const local = "mongodb+srv://duongdvph46500:0522826312@cluster0.2128age.mongodb.net/MD18402";

const connect = async () => {
    try {
        await mongoose.connect(local);
        console.log('Connect success');
    } catch (error) {
        console.error('Connection to MongoDB failed:', error);
    }
}

module.exports = { connect };
