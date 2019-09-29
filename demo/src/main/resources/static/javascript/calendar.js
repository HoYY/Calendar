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

$(function () {
	function deleteSchedule(id) {
		var form = createForm("/schedules/"+id, "post");
		var inputMethod = createInput("_method", "delete");
		var inputScheduleId = createInput("id", id);
		form.appendChild(inputMethod);
		form.appendChild(inputScheduleId);
		document.body.appendChild(form);
		form.submit();
	}
	window.deleteSchedule = deleteSchedule;
});

function createForm(path, method) {
	var form = document.createElement("form");
	form.action = path;
	form.method = method;
	return form;
}

function createInput(name, value) {
	var input = document.createElement("input");
	input.name = name;
	input.value = value;
	input.type = "hidden";
	return input;
}