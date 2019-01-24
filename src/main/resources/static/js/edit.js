$(document)
		.ready(
				function() {
					var languages = $('#lanStr').text().split(",");
					var checkLen = languages.length;
					if (checkLen > 1) {
						var count = checkLen;
						for ( var i in languages) {
							if (document.getElementById(languages[i]) != null) {
								count--;
								document.getElementById(languages[i]).checked = true;
							}
						}
						// means the other checkbox has been check
						if (count != 0) {
							document.getElementById("other").checked = true;
							document.getElementById("oth_input").value = languages[checkLen - 1];
						}
					}

					if ($('#oth_input').val()) {
						$('#oth_input').prop('disabled', false);
					}

				});

function openInput() {
	var checkBox = document.getElementById("other");
	var textInput = document.getElementById("oth_input");

	if (checkBox.checked == true) {
		textInput.disabled = false;
	} else {
		textInput.value = "";
		textInput.disabled = true;
	}
}

function changeDisabled() {
	var box = document.getElementById("other");
	var x = document.getElementById("oth_input");

	if (!(x.value)) {
		box.checked = false;
		x.disabled = true;
	}
}

function check() {
	var otherCheckBox = document.getElementById("other");
	var checked = otherCheckBox.checked;
	if (checked) {
		var otherStr = document.getElementById("oth_input").value;
		otherCheckBox.value = otherStr;
	}
}