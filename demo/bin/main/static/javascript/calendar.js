$(function () {
    $('[data-toggle="tooltip"]').tooltip()
});

$(function () {
    $('#view li:first-child a').tab('show')
});

$(function () {
    $('[data-toggle="popover"]').popover().on('inserted.bs.popover')
});

$('.week, .daily-calendar').click(function() {
    $('#registerSchedule').modal('show');
});

$(".event-consecutive, .event, .event-repeated").click(function(event) {
    event.stopPropagation();
});

//$('#registerSchedule').on('shown.bs.modal', function() {
//    $('#datetimepicker2').css('z-index','3000');
//}); 

$(function () {
    $('#datetimepicker1').datetimepicker({
        format: 'L'
    });
    $('#datetimepicker3').datetimepicker({
        format: 'L'
    });
});

$(function () {
    $('#datetimepicker2').datetimepicker({
        format: 'LT'
    });
    $('#datetimepicker4').datetimepicker({
        format: 'LT'
    });
});

$(function(){
	$('#datetimepicker').datetimepicker({format:"YYYY-MM-DD HH:mm"}).data('DateTimePicker');
	$('#timepicker').datetimepicker({format:"HH:mm"}).data('TimePicker');
});
