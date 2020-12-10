$(function() {
	
	$("form[name='newClient']").validate({
		errorClass : "is-invalid text-danger",
		rules : {
			nom : "required",
			mail : {
				"email" : true,
				"required" : true,
				"maxlength" : 255
			}
		},
		messages : {
			nom : "Please enter your nom",
			mail : {
				email : "format invalide",
				required : "obligatoire",
				maxlength : "email trop long"
			}
		},
		submitHandler : function(form) {
			form.submit();
		}
	});

	$("form[name='login']").validate({
		errorClass : "is-invalid text-danger",
		rules : {
			password : "required",
			username : {
				"email" : true,
				"required" : true,
				"maxlength" : 255
			}
		},
		messages : {
			password : "Obligatoire",
			username : {
				email : "format email invalide",
				required : "obligatoire",
				maxlength : "email trop long"
			}
		},
		submitHandler : function(form) {
			form.submit();
		}
	});
});