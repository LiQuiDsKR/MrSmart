/*---------------------------------------------
Template name :  My Website
Version       :  1.0
Author        :  ThemeLooks
Author url    :  http://themelooks.com


** Block Ui

----------------------------------------------*/

$(function () {
	$(document).ready(function () {
		// 			Block Examples			//
		//----------------------------

		var $white = '#fff';
		$('.block-element').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message: '<i class="icofont-spinner icon-spin"></i>',
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					backgroundColor: 'transparent',
				},
			});
		});

		// Block sidebar
		$('.block-sidebar').on('click', function () {
			var block_ele = $('.sidebar');
			$(block_ele).block({
				message: '<i class="icofont-spinner icon-spin"></i>',
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					backgroundColor: 'transparent',
				},
			});
		});

		// Block page
		$('.block-page').on('click', function () {
			$.blockUI({
				message: '<i class="icofont-spinner icon-spin"></i>',
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					backgroundColor: 'transparent',
				},
			});
		});

		//     Message Options			//
		// --------------------------

		// Spinner only
		$('.block-spinner-only').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message: '<i class="icofont-spinner icon-spin"></i>',
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					backgroundColor: 'transparent',
				},
			});
		});

		// Defult Message
		$('.block-default-message').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message: '<span class="semibold"> Loading...</span>',
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					backgroundColor: 'transparent',
				},
			});
		});

		// Custom Message
		$('.block-custom-message').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message: '<span class="semibold"> Custom Message...</span>',
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					backgroundColor: 'transparent',
				},
			});
		});

		// Message Animation
		$('.block-msg-animation').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message:
					'<div class="semibold"><i class="icofont-spinner icon-spin"></i>&nbsp; Loading ...</div>',
				fadeIn: 1000,
				fadeOut: 1000,
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: '10px 15px',
					color: $white,
					width: 'auto',
					backgroundColor: '#333',
				},
			});
		});

		// Multiple messages
		$('.block-multiple-msgs').on('click', function () {
			var message = $('.multiple-msgs');
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message: message,
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					// width: 200,
					border: 0,
					padding: 0,
					backgroundColor: 'transparent',
				},
				onBlock: function () {
					clearTimeout();
				},
			});

			window.setTimeout(function () {
				message.html(
					'<div class="semibold"><span class="bx bx-revision icon-spin text-left"></span>&nbsp; Loading ...</div>'
				);
			}, 0);

			window.setTimeout(function () {
				message.html('<div class="semibold">Just a minute...</div>');
			}, 2000);

			window.setTimeout(function () {
				message
					.addClass('bg-success')
					.html(
						'<div class="semibold"><span class="icon-thumbs-o-up text-left"></span>&nbsp; Success</div>'
					);
			}, 4000);

			window.setTimeout(function () {
				$(block_ele).unblock({
					onUnblock: function () {
						message.removeClass('bg-success');
					},
				});
			}, 6000);
		});

		// Non centered message
		$('.block-non-centered-msg').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message:
					'<div class="semibold"><i class="icofont-spinner icon-spin"></i>&nbsp; Loading ...</div>',
				timeout: 2000, //unblock after 2 seconds
				centerX: 0,
				centerY: 0,
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					top: '10px',
					left: '',
					right: '10px',
					border: 0,
					padding: '10px 15px',
					backgroundColor: 'transparent',
				},
			});
		});

		//    Overlay Examples			//
		// --------------------------

		// Light overlay
		$('.block-light-overlay').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message: '<i class="icofont-spinner icon-spin"></i>',
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					backgroundColor: 'transparent',
				},
			});
		});

		// Custom overlay
		$('.block-opaque-overlay').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message: '<i class="icofont-spinner icon-spin"></i>',
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: '#8280FD',
					opacity: 1,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					color: $white,
					backgroundColor: 'transparent',
				},
			});
		});

		// Overlay with custom color
		$('.block-custom-overlay').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message: '<i class="icofont-spinner icon-spin"></i>',
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: '#09D1DE',
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					color: $white,
					backgroundColor: 'transparent',
				},
			});
		});

		//			Callback Options			//
		// ---------------------------

		// onBlock callback
		$('.onblock-callback').on('click', function () {
			$.blockUI({
				message: '<i class="icofont-spinner icon-spin"></i>',
				fadeIn: 1000,
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					color: '#333',
					backgroundColor: 'transparent',
				},
				onBlock: function () {
					alert('Page is now blocked, FadeIn complete.');
				},
			});
		});

		// onUnblock callback
		$('.onunblock-callback').on('click', function () {
			$.blockUI({
				message: '<i class="icofont-spinner icon-spin"></i>',
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					color: '#333',
					backgroundColor: 'transparent',
				},
				onUnblock: function () {
					alert('Page is now unblocked. FadeOut complete.');
				},
			});
		});

		// Overlay callback
		$('.onoverlay-click-callback').on('click', function () {
			$.blockUI({
				message: '<i class="icofont-spinner icon-spin"></i>',
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					color: '#333',
					border: 0,
					padding: 0,
					backgroundColor: 'transparent',
				},
				onOverlayClick: $.unblockUI,
			});
		});

		//			Other Options			//
		//-------------------------

		// Block Without Message
		$('.block-without-msg').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message: null,
				timeout: 2000, //unblock after 2 seconds
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					backgroundColor: 'transparent',
				},
			});
		});

		// Block without overlay
		$('.block-without-overlay').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message: '<i class="icofont-spinner icon-spin"></i>',
				showOverlay: false,
				timeout: 2000, //unblock after 2 seconds
				css: {
					width: 50,
					height: 50,
					lineHeight: 1,
					color: $white,
					border: 0,
					padding: 15,
					backgroundColor: '#333',
				},
			});
		});

		// Unblock on overlay click
		$('.overlay-unblock').on('click', function () {
			var block_ele = $(this).closest('.card');
			$(block_ele).block({
				message: '<i class="icofont-spinner icon-spin"></i>',
				overlayCSS: {
					backgroundColor: $white,
					opacity: 0.8,
					cursor: 'wait',
				},
				css: {
					border: 0,
					padding: 0,
					backgroundColor: 'transparent',
				},
			});

			$('.blockOverlay').on('click', function () {
				$(block_ele).unblock();
			});
		});

		// Growl notification
		$('.growl-notification').on('click', function () {
			$.blockUI({
				message: $('.growl-notification-example'),
				fadeIn: 700,
				fadeOut: 700,
				timeout: 5000,
				showOverlay: false,
				centerY: false,
				css: {
					width: '250px',
					top: '20px',
					left: '',
					right: '20px',
					border: 'none',
					padding: '15px 5px',
					backgroundColor: '#00CFDD',
					'-webkit-border-radius': '10px',
					'-moz-border-radius': '10px',
					opacity: 0.9,
					color: $white,
				},
			});
		});

		//multiple message and glow notification display none when document ready
		$('.growl-notification-example').css('display', ' none');
		$('.multiple-msgs').css({ display: 'none', padding: '15px 0' });
	});
});
