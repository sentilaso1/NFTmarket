var editor;

$(function () {

    // Rich text editor for product details editing
    const E = window.wangEditor;

    const editorConfig = { MENU_CONF: {} }
    editorConfig.MENU_CONF['uploadImage'] = {
          // Configure server image upload address
          server: '/admin/upload/files',
          // Timeout duration 5s
          timeout: 5 * 1000,
          fieldName: 'files',
          // File type restriction when selecting files, default is ['image/*']
          allowedFileTypes: ['image/*'],
          // Limit image size to 4MB
          maxFileSize: 4 * 1024 * 1024,
          base64LimitSize: 5 * 1024,

          onBeforeUpload(file) {
            console.log('onBeforeUpload', file)

            return file // will upload this file
            // return false // prevent upload
          },
          onProgress(progress) {
            console.log('onProgress', progress)
          },
          onSuccess(file, res) {
            console.log('onSuccess', file, res)
          },
          onFailed(file, res) {
            alert(res.message)
            console.log('onFailed', file, res)
          },
          onError(file, err, res) {
            alert(err.message)
            console.error('onError', file, err, res)
          },
          customInsert: function (result,insertImgFn) {
            if (result != null && result.resultCode == 200) {
                // insertImgFn can insert the image into the editor by passing the image src, and then executing the function
                result.data.forEach(img => {
                    insertImgFn(img)
                });
            } else if (result != null && result.resultCode != 200) {
                Swal.fire({
                    text: result.message,
                    icon: "error", iconColor:"#f05b72",
                });
            } else {
                Swal.fire({
                    text: "error",
                    icon: "error", iconColor:"#f05b72",
                });
            }
          }
    }

    editor = E.createEditor({
      selector: '#editor-text-area',
      html: $(".editor-text").val(),
      config: editorConfig
    })

    const toolbar = E.createToolbar({
      editor,
      selector: '#editor-toolbar',
    })

    // Image upload plugin initialization for product preview image upload
    new AjaxUpload('#uploadGoodsCoverImg', {
        action: '/admin/upload/file',
        name: 'file',
        autoSubmit: true,
        responseType: "json",
        onSubmit: function (file, extension) {
            if (!(extension && /^(jpg|jpeg|png|gif)$/.test(extension.toLowerCase()))) {
                Swal.fire({
                    text: "Only jpg, png, and gif formats are supported!",
                    icon: "error", iconColor:"#f05b72",
                });
                return false;
            }
        },
        onComplete: function (file, r) {
            if (r != null && r.resultCode == 200) {
                $("#goodsCoverImg").attr("src", r.data);
                $("#goodsCoverImg").attr("style", "width: 128px;height: 128px;display:block;");
                return false;
            } else if (r != null && r.resultCode != 200) {
                Swal.fire({
                    text: r.message,
                    icon: "error", iconColor:"#f05b72",
                });
                return false;
            }
            else {
                Swal.fire({
                    text: "error",
                    icon: "error", iconColor:"#f05b72",
                });
            }
        }
    });
});

$('#saveButton').click(function () {
    var goodsId = $('#goodsId').val();
    var goodsCategoryId = $('#levelThree option:selected').val();
    var goodsName = $('#goodsName').val();
    var tag = $('#tag').val();
    var originalPrice = $('#originalPrice').val();
    var sellingPrice = $('#sellingPrice').val();
    var goodsIntro = $('#goodsIntro').val();
    var stockNum = $('#stockNum').val();
    var goodsSellStatus = $("input[name='goodsSellStatus']:checked").val();
    var goodsDetailContent = editor.getHtml();
    var goodsCoverImg = $('#goodsCoverImg')[0].src;
    if (isNull(goodsCategoryId)) {
        Swal.fire({
            text: "Please select a category",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(goodsName)) {
        Swal.fire({
            text: "Please enter the product name",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (!validLength(goodsName, 100)) {
        Swal.fire({
            text: "Product name is too long",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(tag)) {
        Swal.fire({
            text: "Please enter product tags",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (!validLength(tag, 100)) {
        Swal.fire({
            text: "Tag is too long",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(goodsIntro)) {
        Swal.fire({
            text: "Please enter a product introduction",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (!validLength(goodsIntro, 100)) {
        Swal.fire({
            text: "Product introduction is too long",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(originalPrice) || originalPrice < 1) {
        Swal.fire({
            text: "Please enter the product price",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(sellingPrice) || sellingPrice < 1) {
        Swal.fire({
            text: "Please enter the product selling price",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(stockNum) || sellingPrice < 0) {
        Swal.fire({
            text: "Please enter the product stock quantity",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(goodsSellStatus)) {
        Swal.fire({
            text: "Please select the listing status",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(goodsDetailContent)) {
        Swal.fire({
            text: "Please enter the product description",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (!validLength(goodsDetailContent, 50000)) {
        Swal.fire({
            text: "Product description is too long",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    if (isNull(goodsCoverImg) || goodsCoverImg.indexOf('img-upload') != -1) {
        Swal.fire({
            text: "Cover image cannot be empty",
            icon: "error", iconColor:"#f05b72",
        });
        return;
    }
    var url = '/admin/goods/save';
    var swlMessage = 'Save successful';
    var data = {
        "goodsName": goodsName,
        "goodsIntro": goodsIntro,
        "goodsCategoryId": goodsCategoryId,
        "tag": tag,
        "originalPrice": originalPrice,
        "sellingPrice": sellingPrice,
        "stockNum": stockNum,
        "goodsDetailContent": goodsDetailContent,
        "goodsCoverImg": goodsCoverImg,
        "goodsCarousel": goodsCoverImg,
        "goodsSellStatus": goodsSellStatus
    };
    if (goodsId > 0) {
        url = '/admin/goods/update';
        swlMessage = 'Edit successful';
        data = {
            "goodsId": goodsId,
            "goodsName": goodsName,
            "goodsIntro": goodsIntro,
            "goodsCategoryId": goodsCategoryId,
            "tag": tag,
            "originalPrice": originalPrice,
            "sellingPrice": sellingPrice,
            "stockNum": stockNum,
            "goodsDetailContent": goodsDetailContent,
            "goodsCoverImg": goodsCoverImg,
            "goodsCarousel": goodsCoverImg,
            "goodsSellStatus": goodsSellStatus
        };
    }
    console.log(data);
   $.ajax({
        type: 'POST', // Method type
        url: url,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (result) {
            if (result.resultCode == 200) {
                Swal.fire({
                    text: swlMessage,
                    icon: "success", iconColor:"#1d953f",
                    showCancelButton: false,
                    confirmButtonColor: '#1baeae',
                    confirmButtonText: 'Return to product list',
                    confirmButtonClass: 'btn btn-success',
                    buttonsStyling: false
                }).then(function () {
                    window.location.href = "/admin/goods";
                })
            } else {
                Swal.fire({
                    text: result.message,
                    icon: "error", iconColor:"#f05b72",
                });
            }
        },
        error: function () {
            Swal.fire({
                text: "Operation failed",
                icon: "error", iconColor:"#f05b72",
            });
        }
    });
});

$('#cancelButton').click(function () {
    window.location.href = "/admin/goods";
});

$('#levelOne').on('change', function () {
    $.ajax({
        url: '/admin/categories/listForSelect?categoryId=' + $(this).val(),
        type: 'GET',
        success: function (result) {
            if (result.resultCode == 200) {
                var levelTwoSelect = '';
                var secondLevelCategories = result.data.secondLevelCategories;
                var length2 = secondLevelCategories.length;
                for (var i = 0; i < length2; i++) {
                    levelTwoSelect += '<option value=\"' + secondLevelCategories[i].categoryId + '\">' + secondLevelCategories[i].categoryName + '</option>';
                }
                $('#levelTwo').html(levelTwoSelect);
                var levelThreeSelect = '';
                var thirdLevelCategories = result.data.thirdLevelCategories;
                var length3 = thirdLevelCategories.length;
                for (var i = 0; i < length3; i++) {
                    levelThreeSelect += '<option value=\"' + thirdLevelCategories[i].categoryId + '\">' + thirdLevelCategories[i].categoryName + '</option>';
                }
                $('#levelThree').html(levelThreeSelect);
            } else {
                Swal.fire({
                    text: result.message,
                    icon: "error",iconColor:"#f05b72",
                });
            }
            ;
        },
        error: function () {
            Swal.fire({
                text: "Operation failed",
                icon: "error",iconColor:"#f05b72",
            });
        }
    });
});

$('#levelTwo').on('change', function () {
    $.ajax({
        url: '/admin/categories/listForSelect?categoryId=' + $(this).val(),
        type: 'GET',
        success: function (result) {
            if (result.resultCode == 200) {
                var levelThreeSelect = '';
                var thirdLevelCategories = result.data.thirdLevelCategories;
                var length = thirdLevelCategories.length;
                for (var i = 0; i < length; i++) {
                    levelThreeSelect += '<option value=\"' + thirdLevelCategories[i].categoryId + '\">' + thirdLevelCategories[i].categoryName + '</option>';
                }
                $('#levelThree').html(levelThreeSelect);
            } else {
                Swal.fire({
                    text: result.message,
                    icon: "error",iconColor:"#f05b72",
                });
            }
            ;
        },
        error: function () {
            Swal.fire({
                text: "Operation failed",
                icon: "error",iconColor:"#f05b72",
            });
        }
    });
});
