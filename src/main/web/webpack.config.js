const webpack = require('webpack');
const HTMLWebpackPlugin = require('html-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');

module.exports = {
    devtool: "source-map",
    devServer:{
        proxy: {
            '/back/**': {
                target: 'http://10.25.169.133:8084',
                // target: 'http://abdashboard.aihuishou.com',
                changeOrigin: true,
                logLevel: 'debug'
            }
        }
    },
    entry: {
        app: './src/index.jsx'
    },
    output: {
        filename: '[name].[hash].js'
    },
    plugins: [
        new HTMLWebpackPlugin({title: '移动仪表盘|管理后台', template: 'src/index.html'}),
        new CleanWebpackPlugin(['dist'])
    ],
    module: {
        rules: [
            {test: /\.css$/, use: ['style-loader','css-loader']},
            {test: /\.(woff|woff2|eot|ttf|otf|svg)$/,use:'file-loader'},
            {test: /\.(jpg|png)$/, use: "url-loader"},
            { enforce: "pre", test: /\.js$/, loader: "source-map-loader" },
            {
                test: /\.jsx?$/, // babel 转换为兼容性的 js
                exclude: /node_modules/,
                loader: 'babel-loader',
                query: {
                    presets: ["@babel/preset-react"],
                    plugins:[ "@babel/plugin-proposal-class-properties"]
                }
            }
        ]
    }

};