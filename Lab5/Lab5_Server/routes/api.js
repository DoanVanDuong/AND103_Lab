const express = require('express');
const router = express.Router();
const mongoose = require('mongoose');
const JWT = require('jsonwebtoken');
const Upload = require('../config/common/upload');
const Distributors = require('../models/distributors');
const Furits = require('../models/fruits');
const Users = require('../models/users');
const Transporter = require('../config/common/mail');
const SECRETKEY = "FPTPOLYTECHNIC";
router.get('/get-distributor-by-id/:id', async (req, res) => {
    try {
        const { id } = req.params.id;
        const distributor = await Distributors.findById(id);

        if (!distributor) {
            return res.status(404).json({
                status: 404,
                messenger: 'Không tìm thấy distributor',
                data: []
            });
        }

        res.json({
            status: 200,
            messenger: 'Chi tiết distributor',
            data: distributor
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            status: 500,
            messenger: 'Đã xảy ra lỗi',
            data: []
        });
    }
});
// Get list of distributors
router.get('/get-list-distributor', async (req, res) => {
    try {
        const data = await Distributors.find().populate();
        res.json(data);
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "messenger": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

// Get all fruits
router.get('/get-all-fruit', async (req, res) => {
    try {
        const data = await Furits.find().populate();
        res.json({
            "status": 200,
            "messenger": "Danh sách fruit",
            "data": data
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "messenger": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

// Add distributor
router.post('/add-distributor', async (req, res) => {
    try {
        const data = req.body;
        delete data._id;

        const newDistributor = new Distributors(data);
        const result = await newDistributor.save();

        if (result) {
            const list = await Distributors.find().populate();
            res.json({
                status: 200,
                messenger: "Thêm thành công",
                data: list
            });
        } else {
            res.status(400).json({
                status: 400,
                messenger: "Lỗi thêm không thành công",
                data: []
            });
        }
    } catch (error) {
        console.error(error);
        res.status(500).json({
            status: 500,
            messenger: "Đã xảy ra lỗi",
            data: []
        });
    }
});

// Add fruit
router.post('/add-furit', async (req, res) => {
    try {
        const data = req.body;
        const newFruit = new Furits({
            name: data.name,
            quantity: data.quantity,
            price: data.price,
            status: data.status,
            image: data.image,
            description: data.description,
            id_distributor: data.id_distributor
        });
        const result = await newFruit.save();
        if (result) {
            res.json({
                "status": 200,
                "messenger": "Thêm thành công",
                "data": result
            });
        } else {
            res.status(400).json({
                "status": 400,
                "messenger": "Thêm không thành công",
                "data": []
            });
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "messenger": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

// Get fruits by id
router.get('/get-fruit-by-id/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const data = await Furits.findById(id).populate('id_distributor');
        res.json({
            "status": 200,
            "messenger": "Danh sách fruit",
            "data": data
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "messenger": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

// Get fruits by price range
router.get('/get-fruit-in-price', async (req, res) => {
    try {
        const { minPrice, maxPrice } = req.query;
        const query = { price: { $gte: minPrice, $lte: maxPrice } };
        const data = await Furits.find(query, "name quantity price id_distributor")
            .populate('id_distributor')
            .sort({ quantity: -1 })
            .skip(0)
            .limit(2);
        res.json({
            'status': 200,
            'messenger': 'Danh sách fruit',
            'data': data
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "messenger": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

// Get fruits with name containing A or X
router.get('/get-list-fruit-have-name-a-or-x', async (req, res) => {
    try {
        const query = {
            $or: [
                { name: { $regex: 'A' } },
                { name: { $regex: 'X' } }
            ]
        };
        const data = await Furits.find(query, 'name quantity price id_distributor').populate('id_distributor');
        res.json({
            'status': 200,
            'messenger': 'Danh sách fruit',
            'data': data
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "messenger": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

// Update fruit by id
router.put('/update-fruit-by-id/:id', Upload.array('image', 5), async (req, res) => {
    try {
        const { id } = req.params;
        const data = req.body;
        const { files } = req;
        const urlsImage = files.map(file => `${req.protocol}://${req.get("host")}/uploads/${file.filename}`);
        const updatefruit = await Furits.findById(id);

        if (updatefruit) {
            updatefruit.name = data.name ?? updatefruit.name;
            updatefruit.quantity = data.quantity ?? updatefruit.quantity;
            updatefruit.price = data.price ?? updatefruit.price;
            updatefruit.status = data.status ?? updatefruit.status;
            updatefruit.image = urlsImage.length ? urlsImage : updatefruit.image;
            updatefruit.description = data.description ?? updatefruit.description;
            updatefruit.id_distributor = data.id_distributor ?? updatefruit.id_distributor;

            const result = await updatefruit.save();
            if (result) {
                res.json({
                    'status': 200,
                    'messenger': 'Cập nhật thành công',
                    'data': result
                });
            } else {
                res.status(400).json({
                    'status': 400,
                    'messenger': 'Cập nhật không thành công',
                    'data': []
                });
            }
        } else {
            res.status(404).json({
                'status': 404,
                'messenger': 'Không tìm thấy fruit',
                'data': []
            });
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({
            'status': 500,
            'messenger': 'Đã xảy ra lỗi',
            'data': []
        });
    }
});

// Update distributor by id
router.put('/update-distributor-by-id/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const data = req.body;
        const updateDistributor = await Distributors.findById(id);

        if (updateDistributor) {
            updateDistributor.name = data.name ?? updateDistributor.name;

            const result = await updateDistributor.save();
            if (result) {
                const list = await Distributors.find().populate();
                res.json({
                    'status': 200,
                    'messenger': 'Cập nhật thành công',
                    'data': list
                });
            } else {
                res.status(400).json({
                    'status': 400,
                    'messenger': 'Cập nhật không thành công',
                    'data': []
                });
            }
        } else {
            res.status(404).json({
                'status': 404,
                'messenger': 'Không tìm thấy distributor',
                'data': []
            });
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({
            'status': 500,
            'messenger': 'Đã xảy ra lỗi',
            'data': []
        });
    }
});

// Delete fruit by id
router.delete('/destroy-fruit-by-id/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const result = await Furits.findByIdAndDelete(id);
        if (result) {
            res.json({
                "status": 200,
                "messenger": "Xóa thành công",
                "data": result
            });
        } else {
            res.status(400).json({
                "status": 400,
                "messenger": "Lỗi! xóa không thành công",
                "data": []
            });
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "messenger": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

// Delete distributor by id
router.delete('/destroy-distributor-by-id/:id', async (req, res) => {
    try {
        const { id } = req.params;
        const result = await Distributors.findByIdAndDelete(id);
        if (result) {
            res.json({
                "status": 200,
                "messenger": "Xóa thành công",
                "data": result
            });
        } else {
            res.status(400).json({
                "status": 400,
                "messenger": "Lỗi! xóa không thành công",
                "data": []
            });
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "messenger": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

// Register user
router.post('/register', async (req, res) => {
    try {
        const data = req.body;
        const existingUser = await Users.findOne({ email: data.email });
        if (existingUser) {
            return res.status(400).json({
                "status": 400,
                "messenger": "Email đã tồn tại",
                "data": []
            });
        }

        const newUser = new Users(data);
        const result = await newUser.save();
        if (result) {
            res.json({
                "status": 200,
                "messenger": "Đăng ký thành công",
                "data": result
            });
        } else {
            res.status(400).json({
                "status": 400,
                "messenger": "Đăng ký không thành công",
                "data": []
            });
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "messenger": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

// Login user
router.post('/login', async (req, res) => {
    try {
        const { email, password } = req.body;
        const user = await Users.findOne({ email });
        if (user) {
            const token = JWT.sign({ email: user.email, fullName: user.fullName, _id: user._id }, SECRETKEY);
            res.json({
                "status": 200,
                "messenger": "Đăng nhập thành công",
                "data": {
                    email: user.email,
                    fullName: user.fullName,
                    _id: user._id,
                    token: token
                }
            });
        } else {
            res.status(400).json({
                "status": 400,
                "messenger": "Email hoặc mật khẩu không đúng",
                "data": []
            });
        }
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "messenger": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

// Send email
router.post('/send-email', async (req, res) => {
    try {
        const { to, subject, content } = req.body;
        const options = {
            from: 'vinhldph14686@fpt.edu.vn',
            to: to,
            subject: subject,
            html: content
        };
        const info = await Transporter.sendMail(options);
        res.json({
            "status": 200,
            "messenger": "Gửi email thành công",
            "data": info
        });
    } catch (error) {
        console.log(error);
        res.status(500).json({
            "status": 500,
            "messenger": "Đã xảy ra lỗi",
            "data": []
        });
    }
});

module.exports = router;
