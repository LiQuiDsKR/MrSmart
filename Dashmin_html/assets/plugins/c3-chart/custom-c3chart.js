/*---------------------------------------------
Template name :  My Website
Version       :  1.0
Author        :  ThemeLooks
Author url    :  http://themelooks.com


** Custom C3-Chart JS

----------------------------------------------*/
$(function () {
	'use strict';

	$(document).ready(function () {
		//Line Chart
		var line_chart = c3.generate({
			bindto: '#line-chart',
			data: {
				columns: [
					['data1', 30, 200, 100, 400, 150, 250],
					['data2', 50, 20, 10, 40, 15, 25],
				],
			},
		});

		setTimeout(function () {
			line_chart.load({
				columns: [['data1', 230, 190, 300, 500, 300, 400]],
			});
		}, 1000);

		setTimeout(function () {
			line_chart.load({
				columns: [['data3', 130, 150, 200, 300, 200, 100]],
			});
		}, 1500);

		setTimeout(function () {
			line_chart.unload({
				ids: 'data1',
			});
		}, 2000);

		//Spline Chart
		var spline_chart = c3.generate({
			bindto: '#spline-chart',
			data: {
				columns: [
					['data1', 30, 200, 100, 400, 150, 250],
					['data2', 130, 100, 140, 200, 150, 50],
				],
				type: 'spline',
			},
		});

		//Step Chart
		var step_chart = c3.generate({
			bindto: '#step-chart',
			data: {
				columns: [
					['data1', 300, 350, 300, 0, 0, 100],
					['data2', 130, 100, 140, 200, 150, 50],
				],
				types: {
					data1: 'step',
					data2: 'area-step',
				},
			},
		});

		//Area Chart
		var area_chart = c3.generate({
			bindto: '#area-chart',
			data: {
				columns: [
					['data1', 300, 350, 300, 0, 0, 0],
					['data2', 130, 100, 140, 200, 150, 50],
				],
				types: {
					data1: 'area',
					data2: 'area-spline',
				},
			},
		});

		//Scatter Bar Chart
		var chart = c3.generate({
			bindto: '#scatter-bar-chart',
			data: {
				xs: {
					setosa: 'setosa_x',
					versicolor: 'versicolor_x',
				},
				// iris data from R
				columns: [
					[
						'setosa_x',
						3.5,
						3.0,
						3.2,
						3.1,
						3.6,
						3.9,
						3.4,
						3.4,
						2.9,
						3.1,
						3.7,
						3.4,
						3.0,
						3.0,
						4.0,
						4.4,
						3.9,
						3.5,
						3.8,
						3.8,
						3.4,
						3.7,
						3.6,
						3.3,
						3.4,
						3.0,
						3.4,
						3.5,
						3.4,
						3.2,
						3.1,
						3.4,
						4.1,
						4.2,
						3.1,
						3.2,
						3.5,
						3.6,
						3.0,
						3.4,
						3.5,
						2.3,
						3.2,
						3.5,
						3.8,
						3.0,
						3.8,
						3.2,
						3.7,
						3.3,
					],
					[
						'versicolor_x',
						3.2,
						3.2,
						3.1,
						2.3,
						2.8,
						2.8,
						3.3,
						2.4,
						2.9,
						2.7,
						2.0,
						3.0,
						2.2,
						2.9,
						2.9,
						3.1,
						3.0,
						2.7,
						2.2,
						2.5,
						3.2,
						2.8,
						2.5,
						2.8,
						2.9,
						3.0,
						2.8,
						3.0,
						2.9,
						2.6,
						2.4,
						2.4,
						2.7,
						2.7,
						3.0,
						3.4,
						3.1,
						2.3,
						3.0,
						2.5,
						2.6,
						3.0,
						2.6,
						2.3,
						2.7,
						3.0,
						2.9,
						2.9,
						2.5,
						2.8,
					],
					[
						'setosa',
						0.2,
						0.2,
						0.2,
						0.2,
						0.2,
						0.4,
						0.3,
						0.2,
						0.2,
						0.1,
						0.2,
						0.2,
						0.1,
						0.1,
						0.2,
						0.4,
						0.4,
						0.3,
						0.3,
						0.3,
						0.2,
						0.4,
						0.2,
						0.5,
						0.2,
						0.2,
						0.4,
						0.2,
						0.2,
						0.2,
						0.2,
						0.4,
						0.1,
						0.2,
						0.2,
						0.2,
						0.2,
						0.1,
						0.2,
						0.2,
						0.3,
						0.3,
						0.2,
						0.6,
						0.4,
						0.3,
						0.2,
						0.2,
						0.2,
						0.2,
					],
					[
						'versicolor',
						1.4,
						1.5,
						1.5,
						1.3,
						1.5,
						1.3,
						1.6,
						1.0,
						1.3,
						1.4,
						1.0,
						1.5,
						1.0,
						1.4,
						1.3,
						1.4,
						1.5,
						1.0,
						1.5,
						1.1,
						1.8,
						1.3,
						1.5,
						1.2,
						1.3,
						1.4,
						1.4,
						1.7,
						1.5,
						1.0,
						1.1,
						1.0,
						1.2,
						1.6,
						1.5,
						1.6,
						1.5,
						1.3,
						1.3,
						1.3,
						1.2,
						1.4,
						1.2,
						1.0,
						1.3,
						1.2,
						1.3,
						1.3,
						1.1,
						1.3,
					],
				],
				type: 'scatter',
			},
			axis: {
				x: {
					label: 'Sepal.Width',
					tick: {
						fit: false,
					},
				},
				y: {
					label: 'Petal.Width',
				},
			},
		});

		setTimeout(function () {
			chart.load({
				xs: {
					virginica: 'virginica_x',
				},
				columns: [
					[
						'virginica_x',
						3.3,
						2.7,
						3.0,
						2.9,
						3.0,
						3.0,
						2.5,
						2.9,
						2.5,
						3.6,
						3.2,
						2.7,
						3.0,
						2.5,
						2.8,
						3.2,
						3.0,
						3.8,
						2.6,
						2.2,
						3.2,
						2.8,
						2.8,
						2.7,
						3.3,
						3.2,
						2.8,
						3.0,
						2.8,
						3.0,
						2.8,
						3.8,
						2.8,
						2.8,
						2.6,
						3.0,
						3.4,
						3.1,
						3.0,
						3.1,
						3.1,
						3.1,
						2.7,
						3.2,
						3.3,
						3.0,
						2.5,
						3.0,
						3.4,
						3.0,
					],
					[
						'virginica',
						2.5,
						1.9,
						2.1,
						1.8,
						2.2,
						2.1,
						1.7,
						1.8,
						1.8,
						2.5,
						2.0,
						1.9,
						2.1,
						2.0,
						2.4,
						2.3,
						1.8,
						2.2,
						2.3,
						1.5,
						2.3,
						2.0,
						2.0,
						1.8,
						2.1,
						1.8,
						1.8,
						1.8,
						2.1,
						1.6,
						1.9,
						2.0,
						2.2,
						1.5,
						1.4,
						2.3,
						2.4,
						1.8,
						1.8,
						2.1,
						2.4,
						2.3,
						1.9,
						2.3,
						2.5,
						2.3,
						1.9,
						2.0,
						2.3,
						1.8,
					],
				],
			});
		}, 1000);

		setTimeout(function () {
			chart.unload({
				ids: 'setosa',
			});
		}, 2000);

		setTimeout(function () {
			chart.load({
				columns: [
					[
						'virginica',
						0.2,
						0.2,
						0.2,
						0.2,
						0.2,
						0.4,
						0.3,
						0.2,
						0.2,
						0.1,
						0.2,
						0.2,
						0.1,
						0.1,
						0.2,
						0.4,
						0.4,
						0.3,
						0.3,
						0.3,
						0.2,
						0.4,
						0.2,
						0.5,
						0.2,
						0.2,
						0.4,
						0.2,
						0.2,
						0.2,
						0.2,
						0.4,
						0.1,
						0.2,
						0.2,
						0.2,
						0.2,
						0.1,
						0.2,
						0.2,
						0.3,
						0.3,
						0.2,
						0.6,
						0.4,
						0.3,
						0.2,
						0.2,
						0.2,
						0.2,
					],
				],
			});
		}, 3000);

		//Pie Chart
		var pie_chart = c3.generate({
			bindto: '#pie-chart',
			data: {
				// iris data from R
				columns: [
					['data1', 30],
					['data2', 120],
				],
				type: 'pie',
				onclick: function (d, i) {
					console.log('onclick', d, i);
				},
				onmouseover: function (d, i) {
					console.log('onmouseover', d, i);
				},
				onmouseout: function (d, i) {
					console.log('onmouseout', d, i);
				},
			},
		});

		setTimeout(function () {
			chart.load({
				columns: [
					[
						'setosa',
						0.2,
						0.2,
						0.2,
						0.2,
						0.2,
						0.4,
						0.3,
						0.2,
						0.2,
						0.1,
						0.2,
						0.2,
						0.1,
						0.1,
						0.2,
						0.4,
						0.4,
						0.3,
						0.3,
						0.3,
						0.2,
						0.4,
						0.2,
						0.5,
						0.2,
						0.2,
						0.4,
						0.2,
						0.2,
						0.2,
						0.2,
						0.4,
						0.1,
						0.2,
						0.2,
						0.2,
						0.2,
						0.1,
						0.2,
						0.2,
						0.3,
						0.3,
						0.2,
						0.6,
						0.4,
						0.3,
						0.2,
						0.2,
						0.2,
						0.2,
					],
					[
						'versicolor',
						1.4,
						1.5,
						1.5,
						1.3,
						1.5,
						1.3,
						1.6,
						1.0,
						1.3,
						1.4,
						1.0,
						1.5,
						1.0,
						1.4,
						1.3,
						1.4,
						1.5,
						1.0,
						1.5,
						1.1,
						1.8,
						1.3,
						1.5,
						1.2,
						1.3,
						1.4,
						1.4,
						1.7,
						1.5,
						1.0,
						1.1,
						1.0,
						1.2,
						1.6,
						1.5,
						1.6,
						1.5,
						1.3,
						1.3,
						1.3,
						1.2,
						1.4,
						1.2,
						1.0,
						1.3,
						1.2,
						1.3,
						1.3,
						1.1,
						1.3,
					],
					[
						'virginica',
						2.5,
						1.9,
						2.1,
						1.8,
						2.2,
						2.1,
						1.7,
						1.8,
						1.8,
						2.5,
						2.0,
						1.9,
						2.1,
						2.0,
						2.4,
						2.3,
						1.8,
						2.2,
						2.3,
						1.5,
						2.3,
						2.0,
						2.0,
						1.8,
						2.1,
						1.8,
						1.8,
						1.8,
						2.1,
						1.6,
						1.9,
						2.0,
						2.2,
						1.5,
						1.4,
						2.3,
						2.4,
						1.8,
						1.8,
						2.1,
						2.4,
						2.3,
						1.9,
						2.3,
						2.5,
						2.3,
						1.9,
						2.0,
						2.3,
						1.8,
					],
				],
			});
		}, 1500);

		setTimeout(function () {
			chart.unload({
				ids: 'data1',
			});
			chart.unload({
				ids: 'data2',
			});
		}, 2500);

		//Donut Chart
		var donut_chart = c3.generate({
			bindto: '#donut-chart',
			data: {
				columns: [
					['data1', 30],
					['data2', 120],
				],
				type: 'donut',
				onclick: function (d, i) {
					console.log('onclick', d, i);
				},
				onmouseover: function (d, i) {
					console.log('onmouseover', d, i);
				},
				onmouseout: function (d, i) {
					console.log('onmouseout', d, i);
				},
			},
			donut: {
				title: 'Iris Petal Width',
			},
		});

		setTimeout(function () {
			chart.load({
				columns: [
					[
						'setosa',
						0.2,
						0.2,
						0.2,
						0.2,
						0.2,
						0.4,
						0.3,
						0.2,
						0.2,
						0.1,
						0.2,
						0.2,
						0.1,
						0.1,
						0.2,
						0.4,
						0.4,
						0.3,
						0.3,
						0.3,
						0.2,
						0.4,
						0.2,
						0.5,
						0.2,
						0.2,
						0.4,
						0.2,
						0.2,
						0.2,
						0.2,
						0.4,
						0.1,
						0.2,
						0.2,
						0.2,
						0.2,
						0.1,
						0.2,
						0.2,
						0.3,
						0.3,
						0.2,
						0.6,
						0.4,
						0.3,
						0.2,
						0.2,
						0.2,
						0.2,
					],
					[
						'versicolor',
						1.4,
						1.5,
						1.5,
						1.3,
						1.5,
						1.3,
						1.6,
						1.0,
						1.3,
						1.4,
						1.0,
						1.5,
						1.0,
						1.4,
						1.3,
						1.4,
						1.5,
						1.0,
						1.5,
						1.1,
						1.8,
						1.3,
						1.5,
						1.2,
						1.3,
						1.4,
						1.4,
						1.7,
						1.5,
						1.0,
						1.1,
						1.0,
						1.2,
						1.6,
						1.5,
						1.6,
						1.5,
						1.3,
						1.3,
						1.3,
						1.2,
						1.4,
						1.2,
						1.0,
						1.3,
						1.2,
						1.3,
						1.3,
						1.1,
						1.3,
					],
					[
						'virginica',
						2.5,
						1.9,
						2.1,
						1.8,
						2.2,
						2.1,
						1.7,
						1.8,
						1.8,
						2.5,
						2.0,
						1.9,
						2.1,
						2.0,
						2.4,
						2.3,
						1.8,
						2.2,
						2.3,
						1.5,
						2.3,
						2.0,
						2.0,
						1.8,
						2.1,
						1.8,
						1.8,
						1.8,
						2.1,
						1.6,
						1.9,
						2.0,
						2.2,
						1.5,
						1.4,
						2.3,
						2.4,
						1.8,
						1.8,
						2.1,
						2.4,
						2.3,
						1.9,
						2.3,
						2.5,
						2.3,
						1.9,
						2.0,
						2.3,
						1.8,
					],
				],
			});
		}, 1500);

		setTimeout(function () {
			chart.unload({
				ids: 'data1',
			});
			chart.unload({
				ids: 'data2',
			});
		}, 2500);

		//Combination Chart
		var combination_chart = c3.generate({
			bindto: '#combination_chart',
			data: {
				columns: [
					['data1', 30, 20, 50, 40, 60, 50],
					['data2', 200, 130, 90, 240, 130, 220],
					['data3', 300, 200, 160, 400, 250, 250],
					['data4', 200, 130, 90, 240, 130, 220],
					['data5', 130, 120, 150, 140, 160, 150],
					['data6', 90, 70, 20, 50, 60, 120],
				],
				type: 'bar',
				types: {
					data3: 'spline',
					data4: 'line',
					data6: 'area',
				},
				groups: [['data1', 'data2']],
			},
		});

		//Gauge Chart
		var gauge_chart = c3.generate({
			bindto: '#stanford_chart',
			data: {
				x: 'HPE',
				epochs: 'Epochs',
				columns: [
					[
						'HPE',
						2.5,
						2.5,
						2.5,
						2.5,
						2.5,
						3,
						3,
						3,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4.5,
						4.5,
						4.5,
						4.5,
						4.5,
						4.5,
						4.5,
						5,
						5,
						5,
						5,
						5,
						5,
						5,
						5,
						5,
						5,
						5,
						5.5,
						5.5,
						5.5,
						2,
						2.5,
						2.5,
						3,
						3,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						4,
						4,
						4,
						4,
						4,
						4.5,
						4.5,
						4.5,
						4.5,
						4.5,
						5,
						5,
						5,
						5,
						5,
						5,
						5.5,
						5.5,
						2.5,
						3,
						3,
						3.5,
						3.5,
						3.5,
						3.5,
						4,
						4,
						4,
						4,
						4,
						4.5,
						4.5,
						4.5,
						5,
						5,
						5,
						5,
						5,
						5,
						5.5,
						5.5,
						5.5,
						5.5,
						2.5,
						2.5,
						2.5,
						3,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						4,
						4.5,
						4.5,
						4.5,
						4.5,
						4.5,
						5,
						5,
						5,
						5.5,
						2,
						3.5,
						3.5,
						3.5,
						3.5,
						3.5,
						4,
						4.5,
						4.5,
						4.5,
						4.5,
						5,
						2,
						2,
						3,
						3,
						3.5,
						3.5,
						3.5,
						3.5,
						4,
						4,
						4,
						4,
						5,
						2,
						3,
						3,
						3.5,
						3.5,
						3.5,
						3.5,
						4,
						4.5,
						5,
						5,
						5,
						5.5,
						5.5,
						2.5,
						3,
						3,
						3,
						3.5,
						4,
						2.5,
						3,
						3.5,
						4,
						4,
						4.5,
						5,
						3.5,
						4,
						4,
						4,
						4,
						4.5,
						3.5,
						4,
						4.5,
						5,
						5,
						2.5,
						3,
						3.5,
						3.5,
						4,
						4.5,
						4.5,
						4,
						5,
						3,
						4,
						4,
						2,
						4.5,
						3.5,
						2.5,
						3.5,
						4,
						4,
						2.5,
						2.5,
						3,
						3,
						4,
						4.5,
						5,
						5,
						4.5,
						2.5,
						3,
						4,
						3,
						3.5,
						3.5,
						4,
						2.5,
						3.5,
						2.5,
						3.5,
						2.5,
						2.5,
						3.5,
						2.5,
						4.5,
						3,
						4,
						2.5,
						4.5,
						2.5,
						4,
						4,
						2.5,
						3,
						3.5,
						2.5,
						3.5,
						3.5,
						3.5,
						2.5,
						3.5,
						3.5,
						4,
						4,
						3.5,
						4,
						4,
						4,
					],
					[
						'HPL',
						24.5,
						24,
						27.5,
						56.5,
						26.5,
						26,
						51.5,
						50,
						39,
						39.5,
						54,
						48.5,
						54.5,
						53,
						52,
						13.5,
						16.5,
						15.5,
						14.5,
						19,
						19.5,
						41,
						40,
						42.5,
						40.5,
						41.5,
						30,
						56,
						47,
						11.5,
						11,
						12,
						14.5,
						55,
						56.5,
						54,
						55.5,
						56,
						48.5,
						19,
						56,
						56.5,
						53.5,
						51.5,
						52,
						31.5,
						36.5,
						38.5,
						22,
						21,
						22.5,
						37,
						38,
						38.5,
						11,
						55,
						14.5,
						12.5,
						56,
						22,
						11,
						48,
						12.5,
						14,
						17,
						13.5,
						43,
						55.5,
						53.5,
						10.5,
						49.5,
						54.5,
						51.5,
						19.5,
						24,
						52.5,
						49.5,
						47,
						45.5,
						46,
						20,
						34.5,
						37.5,
						28,
						10,
						26.5,
						22.5,
						13,
						18.5,
						20,
						29,
						39.5,
						48.5,
						50.5,
						19.5,
						29,
						27.5,
						52.5,
						50.5,
						53,
						37,
						36,
						34.5,
						20.5,
						31.5,
						33,
						32,
						36,
						29,
						28.5,
						31.5,
						29,
						30,
						11.5,
						49,
						52.5,
						20.5,
						49.5,
						28,
						24.5,
						53,
						50,
						23.5,
						47.5,
						38,
						35,
						34,
						12,
						21,
						36.5,
						51,
						12,
						58.5,
						36.5,
						28.5,
						51,
						50.5,
						20,
						50,
						56,
						55,
						29.5,
						28.5,
						23,
						17.5,
						38.5,
						57.5,
						29.5,
						38.5,
						49,
						52.5,
						34,
						11.5,
						27,
						30,
						10,
						51.5,
						50.5,
						18,
						20.5,
						23,
						49,
						51,
						48,
						33.5,
						32.5,
						27,
						28,
						25.5,
						57.5,
						10.5,
						52,
						29.5,
						27.5,
						50,
						28.5,
						51.5,
						21.5,
						35.5,
						49.5,
						37.5,
						39,
						50,
						51,
						22.5,
						58,
						20,
						25.5,
						48.5,
						32,
						30,
						24.5,
						23.5,
						29.5,
						23,
						25,
						21,
						38,
						32.5,
						12,
						22,
						37,
						55.5,
						22,
						38,
						55.5,
						29,
						23.5,
						21,
						12.5,
						14,
						11.5,
						56.5,
						21.5,
						20.5,
						33,
						33.5,
						27,
						13,
						10.5,
						22.5,
						57,
						24,
						28.5,
						28,
						10,
						37,
						56,
						37.5,
						11,
						10.5,
						28,
						13.5,
						26,
						11,
						27.5,
						12,
						26.5,
						26,
						24.5,
						24,
						25,
						25,
						25,
						11.5,
						25.5,
						26.5,
						26,
						25.5,
						27.5,
						27,
						25,
						27,
						24.5,
						26,
						26.5,
						25.5,
					],
					[
						'Epochs',
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						1,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						2,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						3,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						4,
						5,
						5,
						5,
						5,
						5,
						5,
						5,
						5,
						5,
						5,
						5,
						5,
						6,
						6,
						6,
						6,
						6,
						6,
						6,
						6,
						6,
						6,
						6,
						6,
						6,
						7,
						7,
						7,
						7,
						7,
						7,
						7,
						7,
						7,
						7,
						7,
						7,
						7,
						7,
						8,
						8,
						8,
						8,
						8,
						8,
						9,
						9,
						9,
						9,
						9,
						9,
						9,
						10,
						10,
						10,
						10,
						10,
						10,
						11,
						11,
						11,
						12,
						12,
						13,
						13,
						13,
						13,
						13,
						13,
						13,
						14,
						14,
						15,
						15,
						15,
						16,
						16,
						17,
						18,
						18,
						18,
						18,
						19,
						19,
						19,
						19,
						19,
						20,
						20,
						20,
						22,
						23,
						23,
						23,
						24,
						24,
						24,
						24,
						25,
						28,
						29,
						29,
						36,
						38,
						39,
						43,
						44,
						47,
						50,
						54,
						54,
						59,
						62,
						62,
						70,
						70,
						81,
						84,
						85,
						86,
						88,
						89,
						93,
						94,
						95,
						106,
						110,
						111,
						115,
						170,
					],
				],
				type: 'stanford',
			},
			legend: {
				hide: true,
			},
			point: {
				focus: {
					expand: {
						r: 5,
					},
				},
				r: 2,
			},
			axis: {
				x: {
					show: true,
					label: {
						text: 'HPE (m)',
						position: 'outer-center',
					},
					min: 0,
					max: 61,
					tick: {
						values: d3.range(0, 65, 10),
					},
					padding: {
						top: 0,
						bottom: 0,
						left: 0,
						right: 0,
					},
				},
				y: {
					show: true,
					label: {
						text: 'HPL (m)',
						position: 'outer-middle',
					},
					min: 0,
					max: 60,
					tick: {
						values: d3.range(0, 65, 10),
					},
					padding: {
						top: 5,
						bottom: 0,
						left: 0,
						right: 0,
					},
				},
			},
			stanford: {
				scaleMin: 1,
				scaleMax: 10000,
				scaleFormat: 'pow10',
				padding: {
					top: 15,
					right: 0,
					bottom: 0,
					left: 0,
				},
			},
		});
	});
});
