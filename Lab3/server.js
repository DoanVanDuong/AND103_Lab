const { log } = require('console');
const express = require('express');
const { mongo, default: mongoose } = require('mongoose');
const app = express();
const port = 3000;
app.listen(port, () => {
    console.log('Example app listening on port');
});
const uri = 'mongodb+srv://root:0522826312@cluster0.2128age.mongodb.net/MD18402';
const carModel = require('./carModel');
app.get('/', async (req, res) => {
    await mongoose.connect(uri);
    let cars = await carModel.find();
    console.log(cars);
    res.send(cars)
});
app.get('/xoa:id', async (req, res) => {
    await mongoose.connect(uri);
})