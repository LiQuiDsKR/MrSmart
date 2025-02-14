/*---------------------------------------------
Template name :  My Website
Version       :  1.0
Author        :  ThemeLooks
Author url    :  http://themelooks.com


** Custom Toastr JS

----------------------------------------------*/

$(function () {
	'use strict';

	$(document).ready(function () {
		$('#type-success').on('click', function () {
			toastr.success('Have fun storming the castle!', 'Miracle Max Says');
		}),
			$('#type-info').on('click', function () {
				toastr.info(
					'We do have the Kapua suite available.',
					'Turtle Bay Resort'
				);
			}),
			$('#type-warning').on('click', function () {
				toastr.warning(
					'My name is Inigo Montoya. You killed my father, prepare to die!'
				);
			}),
			$('#type-error').on('click', function () {
				toastr.error(
					'I do not think that word means what you think it means.',
					'Inconceivable!'
				);
			}),
			$('#position-top-left').on('click', function () {
				toastr.info(
					'I do not think that word means what you think it means.',
					'Top Left!',
					{ positionClass: 'toast-top-left', containerId: 'toast-top-left' }
				);
			}),
			$('#position-top-center').on('click', function () {
				toastr.info(
					'I do not think that word means what you think it means.',
					'Top Center!',
					{ positionClass: 'toast-top-center', containerId: 'toast-top-center' }
				);
			}),
			$('#position-top-right').on('click', function () {
				toastr.info(
					'I do not think that word means what you think it means.',
					'Top Right!',
					{ positionClass: 'toast-top-right', containerId: 'toast-top-right' }
				);
			}),
			$('#position-top-full').on('click', function () {
				toastr.info(
					'I do not think that word means what you think it means.',
					'Top Full Width!',
					{ positionClass: 'toast-top-full-width' }
				);
			}),
			$('#position-bottom-left').on('click', function () {
				toastr.info(
					'I do not think that word means what you think it means.',
					'Bottom Left!',
					{
						positionClass: 'toast-bottom-left',
						containerId: 'toast-bottom-left',
					}
				);
			}),
			$('#position-bottom-center').on('click', function () {
				toastr.info(
					'I do not think that word means what you think it means.',
					'Bottom Center!',
					{
						positionClass: 'toast-bottom-center',
						containerId: 'toast-bottom-center',
					}
				);
			}),
			$('#position-bottom-right').on('click', function () {
				toastr.info(
					'I do not think that word means what you think it means.',
					'Bottom Right!',
					{
						positionClass: 'toast-bottom-right',
						containerId: 'toast-bottom-right',
					}
				);
			}),
			$('#position-bottom-full').on('click', function () {
				toastr.info(
					'I do not think that word means what you think it means.',
					'Bottom Full Width!',
					{ positionClass: 'toast-bottom-full-width' }
				);
			}),
			$('#text-notification').on('click', function () {
				toastr.info('Have fun storming the castle!', 'Miracle Max Says');
			}),
			$('#close-button').on('click', function () {
				toastr.success('Have fun storming the castle!', 'With Close Button', {
					closeButton: !0,
				});
			}),
			$('#progress-bar').on('click', function () {
				toastr.warning('Have fun storming the castle!', 'Progress Bar', {
					progressBar: !0,
				});
			}),
			$('#clear-toast-btn').on('click', function () {
				toastr.error(
					'Clear itself?<br /><br /><button type="button" class="btn btn-primary clear">Yes</button>',
					'Clear Toast Button'
				);
			}),
			$('#show-remove-toast').on('click', function () {
				toastr.info('Have fun storming the castle!', 'Miracle Max Says');
			}),
			$('#remove-toast').on('click', function () {
				toastr.remove();
			}),
			$('#show-clear-toast').on('click', function () {
				toastr.info('Have fun storming the castle!', 'Miracle Max Says');
			}),
			$('#clear-toast').on('click', function () {
				toastr.clear();
			}),
			$('#fast-duration').on('click', function () {
				toastr.success('Have fun storming the castle!', 'Fast Duration', {
					showDuration: 500,
				});
			}),
			$('#slow-duration').on('click', function () {
				toastr.warning('Have fun storming the castle!', 'Slow Duration', {
					hideDuration: 3e3,
				});
			}),
			$('#timeout').on('click', function () {
				toastr.error(
					'I do not think that word means what you think it means.',
					'Timeout!',
					{ timeOut: 5e3 }
				);
			}),
			$('#sticky').on('click', function () {
				toastr.info(
					'I do not think that word means what you think it means.',
					'Sticky!',
					{ timeOut: 0 }
				);
			}),
			$('#slide-toast').on('click', function () {
				toastr.success(
					'I do not think that word means what you think it means.',
					'Slide Down / Slide Up!',
					{ showMethod: 'slideDown', hideMethod: 'slideUp', timeOut: 2e3 }
				);
			}),
			$('#fade-toast').on('click', function () {
				toastr.success(
					'I do not think that word means what you think it means.',
					'Slide Down / Slide Up!',
					{ showMethod: 'fadeIn', hideMethod: 'fadeOut', timeOut: 2e3 }
				);
			});
	});
});
