const express = require('express');
const app = express();
const port = 3000;
const bodyParser = require('body-parser');
const mongoose = require('mongoose');
const COMMON = require('./COMMON');
const carModel = require('./carModel');
const apiMobile = require('./api');
const cartModel = require('./cartModel');
const billModel = require('./billModel');
const billDetailModel = require('./billDetailModel');
const uri = COMMON.uri;

// Middleware to parse JSON and URL-encoded data
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`);
});

app.use('/api', apiMobile);

app.get('/', async (req, res) => {
    await mongoose.connect(uri);

    let cars = await carModel.find();

    console.log(cars);

    res.send(cars);
});

app.post('/add_xe', async (req, res) => {
   await mongoose.connect(COMMON.uri, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
    serverSelectionTimeoutMS: 30000,  // 30 seconds
    socketTimeoutMS: 45000,           // 45 seconds
});
    let car = req.body;

    console.log(car);

    let kq = await carModel.create(car);
    console.log(kq);

    let cars = await carModel.find();

    res.send(cars);
});

app.get('/xoa/:id', async (req, res) => {
    await mongoose.connect(uri);

    let id = req.params.id;
    console.log(id);

    await carModel.deleteOne({ _id: id });

    res.redirect('../');
});

app.put('/update/:id', async (req, res) => {
    try {
        await mongoose.connect(uri);
        console.log('Kết nối DB thành công');

        let id = req.params.id;
        let carUpdates = req.body;

        let updatedCar = await carModel.findByIdAndUpdate(id, carUpdates, { new: true });

        if (updatedCar) {
            res.send(updatedCar);
        } else {
            res.status(404).send({ error: 'Car not found' });
        }
    } catch (err) {
        console.error(err);
        res.status(500).send('Error updating car');
    } finally {
        mongoose.connection.close();
    }
});
app.post('/cart', async (req, res) => {
    try {
        const newCart = await cartModel.create(req.body);
        res.send(newCart);
    } catch (err) {
        res.status(500).send('Lỗi khi tạo giỏ hàng');
    }
});

app.get('/cart/:userId', async (req, res) => {
    try {
        const cart = await cartModel.findOne({ userId: req.params.userId });
        res.send(cart);
    } catch (err) {
        res.status(500).send('Lỗi khi lấy giỏ hàng');
    }
});

app.put('/cart/:userId', async (req, res) => {
    try {
        const updatedCart = await cartModel.findOneAndUpdate(
            { userId: req.params.userId },
            { $set: req.body },
            { new: true }
        );
        res.send(updatedCart);
    } catch (err) {
        res.status(500).send('Lỗi khi cập nhật giỏ hàng');
    }
});

// Route cho bảng Bill
app.post('/bill', async (req, res) => {
    try {
        const newBill = await billModel.create(req.body);
        res.send(newBill);
    } catch (err) {
        res.status(500).send('Lỗi khi tạo hóa đơn');
    }
});

app.get('/bill/:userId', async (req, res) => {
    try {
        const bills = await billModel.find({ userId: req.params.userId });
        res.send(bills);
    } catch (err) {
        res.status(500).send('Lỗi khi lấy hóa đơn');
    }
});

app.put('/bill/:id', async (req, res) => {
    try {
        const updatedBill = await billModel.findByIdAndUpdate(
            req.params.id,
            { $set: req.body },
            { new: true }
        );
        res.send(updatedBill);
    } catch (err) {
        res.status(500).send('Lỗi khi cập nhật hóa đơn');
    }
});

// Route cho bảng BillDetail
app.post('/billdetail', async (req, res) => {
    try {
        const newBillDetail = await billDetailModel.create(req.body);
        res.send(newBillDetail);
    } catch (err) {
        res.status(500).send('Lỗi khi tạo chi tiết hóa đơn');
    }
});

app.get('/billdetail/:id', async (req, res) => {
    try {
        const billDetail = await billDetailModel.findById(req.params.id);
        res.send(billDetail);
    } catch (err) {
        res.status(500).send('Lỗi khi lấy chi tiết hóa đơn');
    }
});

app.put('/billdetail/:id', async (req, res) => {
    try {
        const updatedBillDetail = await billDetailModel.findByIdAndUpdate(
            req.params.id,
            { $set: req.body },
            { new: true }
        );
        res.send(updatedBillDetail);
    } catch (err) {
        res.status(500).send('Lỗi khi cập nhật chi tiết hóa đơn');
    }
});
