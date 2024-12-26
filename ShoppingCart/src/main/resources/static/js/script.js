// User register validation

$(function () {

    var userRegister = $("#userRegister");

    userRegister.validate({
        rules: {
            name: {
                required: true,
                lettersonly: true,
                minlength: 3,
                maxlength: 30
            },
            email: {
                required: true,
                space: true,
                email: true
            },
            mobileNumber: {
                required: true,
                space: true,
                numericOnly: true,
                minlength: 10,
                maxlength: 12

            },
            password: {
                required: true,
                space: true,
                minlength: 8,
                maxlength: 30
            },
            confirmPassword: {
                required: true,
                space: true,
                equalTo: '#password'
            },
            address: {
                required: true,
                all: true
            },
            city: {
                required: true,
                space: true
            },
            state: {
                required: true,
            },
            pincode: {
                required: true,
                space: true,
                numericOnly: true
            }, img: {
                required: true,
            }
        },
        messages: {
            name: {
                required: 'Required field',
                lettersonly: "The name must contain only letters",
                minlength: "Name must be at least 3 characters long",
                maxlength: "Name must not exceed the length of 30 characters"
            },
            email: {
                required: 'Required field',
                space: 'Spaces are not allowed',
                email: 'Invalid email'
            },
            mobileNumber: {
                required: 'Required field',
                space: 'Spaces are not allowed',
                numericOnly: 'invalid mob no',
                minlength: 'min 10 digit',
                maxlength: 'max 12 digit'
            },
            password: {
                required: 'Required field',
                space: 'Spaces are not allowed',
                minlength: "Password must contain 8 characters",
                maxlength: "Password must not exceed 30 characters"
            },
            confirmPassword: {
                required: 'Required field',
                space: 'Spaces are not allowed',
                equalTo: 'Does not match with password you provided'
            },
            address: {
                required: 'Required field',
                all: 'invalid'
            },
            city: {
                required: 'Required field',
                space: 'Spaces are not allowed'
            },
            state: {
                required: 'Required field',
                space: 'Spaces are not allowed'
            },
            pincode: {
                required: 'Required field',
                space: 'Spaces are not allowed',
                numericOnly: 'invalid pincode'
            },
            img: {
                required: 'image required',
            }
        }
    })

    // orders validation

    var orders = $("#order");

    orders.validate({
        rules:{
            firstName:{
                required:true,
                lettersonly:true
            },
            lastName:{
                required:true,
                lettersonly:true
            },
            email: {
                required: true,
                space: true,
                email: true
            },
            mobileNumber: {
                required: true,
                space: true,
                numericOnly: true,
                minlength: 10,
                maxlength: 12
            },
            address: {
                required: true,
                all: true
            },
            city: {
                required: true,
                space: true
            },
            state: {
                required: true,
            },
            pincode: {
                required: true,
                space: true,
                numericOnly: true

            },
            paymentMethod:{
                required: true
            }
        },
        messages:{
            firstName:{
                required:'Required field',
                lettersonly:'Only letters allowed'
            },
            lastName:{
                required:'Required field',
                lettersonly:'Only letters allowed'
            },
            email: {
                required: 'Required field',
                space: 'Spaces not allowed',
                email: 'Invalid email'
            },
            mobileNumber: {
                required: 'Required field',
                space: 'Spaces not allowed',
                numericOnly: 'Only numeric symbols allowed',
                minlength: 'This is below minimum allowed number of digits',
                maxlength: 'Exceeds maximum allowed number of digits'
            },
            address: {
                required: 'Required field',
                all: 'invalid'
            },
            city: {
                required: 'Required field',
                space: 'Spaces not allowed'
            },
            state: {
                required: 'Required field',
                space: 'Spaces not allowed'
            },
            pincode: {
                required: 'Required field',
                space: 'Spaces not allowed',
                numericOnly: 'Only numeric symbols allowed'
            },
            paymentMethod:{
                required: 'select payment method'
            }
        }
    })

    // Reset password validation

    var resetPassword = $("#resetPassword")

    resetPassword.validate({
        rules: {
            password: {
                required: true,
                space: true,
                minlength: 8,
                maxlength: 30
            },
            confirmPassword: {
                required: true,
                space: true,
                equalTo: '#password'
            }
        },
        messages: {
            password: {
                required: 'Required field',
                space: 'Spaces are not allowed',
                minlength: "Password must contain 8 characters",
                maxlength: "Password must not exceed 30 characters"
            },
            confirmPassword: {
                required: 'Required field',
                space: 'Spaces are not allowed',
                equalTo: 'password mismatch'
            }
        }
    })
})



jQuery.validator.addMethod('lettersonly', function (value, element) {
    return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
});

jQuery.validator.addMethod('space', function (value, element) {
    return /^[^-\s]+$/.test(value);
});

jQuery.validator.addMethod('all', function (value, element) {
    return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
});

jQuery.validator.addMethod('numericOnly', function (value, element) {
    return /^[0-9]+$/.test(value);
});