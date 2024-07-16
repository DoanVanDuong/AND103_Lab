const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');  

const app = express();
const port = 3000;


app.use(bodyParser.json());

const uri = 'mongodb+srv://duongdvph46500:0522826312@cluster0.2128age.mongodb.net/MD18402';
const CarModel = require('./carModel');

// Kết nối MongoDB
mongoose.connect(uri, { useNewUrlParser: true, useUnifiedTopology: true })
    .then(() => console.log('Connected to MongoDB'))
    .catch(error => console.error('Error connecting to MongoDB:', error));

app.listen(port, () => {
    console.log(`Example app listening on port ${port}`);
});

// Endpoint để hiển thị danh sách xe
app.get('/', async (req, res) => {
    try {
        let cars = await CarModel.find();
        console.log('Cars found:', cars);
        res.send(cars);
    } catch (error) {
        console.error('Error occurred:', error.message);
        res.status(500).send(`Error fetching cars: ${error.message}`);
    }
});


// Endpoint để thêm một xe mới
app.post('/them', async (req, res) => {
    try {
        const newCar = new CarModel(req.body);
        await newCar.save();
        console.log('Car added:', newCar);
        res.status(201).send(newCar);
    } catch (error) {
        console.error('Error occurred:', error.message);
        res.status(500).send(`Error adding the car: ${error.message}`);
    }
});

// Endpoint để xóa một xe theo ID
app.delete('/xoa/:id', async (req, res) => {
    try {
        const carId = req.params.id;
        const result = await CarModel.findByIdAndDelete(carId);

        if (result) {
            res.send(`Deleted car with id: ${carId}`);
        } else {
            res.status(404).send('Car not found');
        }
    } catch (error) {
        console.error('Error occurred:', error.message);
        res.status(500).send(`Error deleting the car: ${error.message}`);
    }
});

app.put('/sua/:id', async (req, res) => {
    try {
        const carId = req.params.id;
        const updatedCar = await CarModel.findByIdAndUpdate(carId, req.body, { new: true, runValidators: true });

        if (updatedCar) {
            res.send(updatedCar);
        } else {
            res.status(404).send('Car not found');
        }
    } catch (error) {
        console.error('Error occurred:', error.message);
        res.status(500).send(`Error updating the car: ${error.message}`);
    }
});