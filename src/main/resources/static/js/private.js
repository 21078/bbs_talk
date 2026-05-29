Date.prototype.format = function (format)
{
    var o = {
        "M+": this.getMonth() + 1, //month
        "d+": this.getDate(),    //day
        "h+": this.getHours(),   //hour
        "m+": this.getMinutes(), //minute
        "s+": this.getSeconds(), //second
        "q+": Math.floor((this.getMonth() + 3) / 3),  //quarter
        "S": this.getMilliseconds() //millisecond
    }
    if (/(y+)/.test(format)) format = format.replace(RegExp.$1,
        (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o) if (new RegExp("(" + k + ")").test(format))
        format = format.replace(RegExp.$1,
            RegExp.$1.length == 1 ? o[k] :
                ("00" + o[k]).substr(("" + o[k]).length));
    return format;
}
$(function ()
{
    $("#loginButton").click(function ()
    {
        login();
    });

    $("#loginYzm").keyup(function (event)
    {
        if (event.which == 13)
            login();
    });

    $("#registerButton").click(function ()
    {
        var uname = $("#registerUname").val();
        var upwd = $("#registerUpwd").val();
        var confirmPwd = $("#registerconfirmUpwd").val();

        var flag = true;

        if (uname.length > 16)
        {
            $("#registerUname+span").html("X").css("background-color", "red");
            flag = false;
        }
        else
            $("#registerUname+span").html("").css("background-color", "#EEEEEE");

        if ((upwd.length > 16) || (upwd.length < 6))
        {
            $("#registerUpwd+span").html("密码长度需6-16位").css("background-color", "red");
            flag = false;
        }
        else
            $("#registerUpwd+span").html("").css("background-color", "#EEEEEE");

        if (upwd != confirmPwd)
        {
            $("#registerconfirmUpwd+span").html("X").css("background-color", "red");
            flag = false;
        }
        else
            $("#registerconfirmUpwd+span").html("").css("background-color", "#EEEEEE");



        if (flag)
        {
            var data = {"uname": uname, "upwd": upwd};
            $.post("/register.do", data, function (data)
            {
                alert(data);
                if (data == "注册成功")
                    location.reload()
            });
        }
        else
        {
            if (uname.length > 16) {
                alert("用户名不能超过16个字符");
            } else if ((upwd.length > 16) || (upwd.length < 6)) {
                alert("密码长度必须在6-16位之间");
            } else if (upwd != confirmPwd) {
                alert("两次输入的密码不一致");
            }
        }
    });
    $("#logouot").click(function ()
    {
        $.get("/logout.do", {}, function (data)
        {
            alert(data);
            if (data == "退出成功")
                location.reload()
        });
    });


    $("#person").click(function ()
    {
        window.location.href = '/person.do';
    });


    // 监听板块选择，如果是问题板块则显示奖励输入框
    $("#sendPostCategory").change(function () {
        if ($(this).val() === "问题") {
            $("#sendPostPrize").show();
        } else {
            $("#sendPostPrize").hide();
            $("#sendPostPrize").val("");
        }
    });

    // 图片预览功能
    $("#coverImage").change(function () {
        var file = this.files[0];
        if (file) {
            // 检查文件大小
            if (file.size > 5 * 1024 * 1024) {
                alert("图片大小不能超过5MB");
                $(this).val("");
                return;
            }

            // 检查文件类型
            if (!file.type.match('image/(jpeg|jpg|png)')) {
                alert("只支持JPG和PNG格式的图片");
                $(this).val("");
                return;
            }

            // 显示文件大小信息
            var fileSizeMB = (file.size / (1024 * 1024)).toFixed(2);
            $("#fileSizeText").text("文件大小: " + fileSizeMB + " MB");
            $("#fileSizeWarning").show();

            // 创建图片预览
            var reader = new FileReader();
            reader.onload = function (e) {
                $("#previewImage").attr("src", e.target.result);
                $("#imagePreview").show();
            }
            reader.readAsDataURL(file);
        } else {
            $("#imagePreview").hide();
            $("#fileSizeWarning").hide();
        }
    });

    // 移除预览图片
    $("#removeImage").click(function () {
        $("#coverImage").val("");
        $("#imagePreview").hide();
        $("#fileSizeWarning").hide();
    });

    // 图片预览功能
    $("#coverImage").change(function () {
        var file = this.files[0];
        if (file) {
            // 检查文件大小
            var fileSize = file.size;
            var maxSize = 5 * 1024 * 1024; // 5MB
            if (fileSize > maxSize) {
                alert("图片大小不能超过5MB");
                this.value = ''; // 清空文件选择
                return;
            }

            // 显示文件大小信息
            var sizeText = "文件大小: " + (fileSize / 1024 / 1024).toFixed(2) + " MB";
            $("#fileSizeText").text(sizeText);
            $("#fileSizeWarning").show();

            // 创建图片预览
            var reader = new FileReader();
            reader.onload = function (e) {
                $("#previewImage").attr('src', e.target.result);
                $("#imagePreview").show();
            }
            reader.readAsDataURL(file);
        } else {
            $("#imagePreview").hide();
            $("#fileSizeWarning").hide();
        }
    });

    // 移除图片预览
    $("#removeImage").click(function () {
        $("#coverImage").val(''); // 清空文件输入
        $("#imagePreview").hide();
        $("#fileSizeWarning").hide();
    });

    $("#sendPostButton").click(function ()
    {
        var ptitle = $("#sendPostTitle").val();
        var pbody = $("#sendPostBody").val();
        var category = $("#sendPostCategory").val();
        var prize = $("#sendPostPrize").val();
        var coverImage = $("#coverImage")[0].files[0];

        if ((ptitle.length > 0 && ptitle.length <= 30) && (pbody.length > 0 && pbody.length < 1000) && category !== "")
        {
            // 如果是问题板块，验证奖励积分
            if (category === "问题") {
                if (!prize || prize < 1 || prize > 10) {
                    alert("问题板块必须设置奖励积分(1-10)");
                    return;
                }
            }

            // 验证图片文件大小（最大5MB）
            if (coverImage && coverImage.size > 5 * 1024 * 1024) {
                alert("图片大小不能超过5MB");
                return;
            }

            // 验证图片文件类型（只允许PNG和JPG）
            if (coverImage && !coverImage.type.match('image/(jpeg|jpg|png)')) {
                alert("只支持JPG和PNG格式的图片");
                return;
            }

            // 显示加载状态
            var $sendButton = $("#sendPostButton");
            var originalText = $sendButton.html();
            $sendButton.html('<span class="glyphicon glyphicon-refresh glyphicon-spin"></span> 发布中...').prop('disabled', true);

            // 添加加载提示
            var loadingAlert = $('<div class="alert alert-info" style="position: fixed; top: 20px; right: 20px; z-index: 9999; display: none;">')
                .html('<span class="glyphicon glyphicon-info-sign"></span> 正在发布帖子')
                .appendTo('body')
                .fadeIn();

            // 如果有图片，添加图片上传提示
            if (coverImage) {
                loadingAlert.append('<br><small>正在上传封面图片...</small>');
            }

            var formData = new FormData();
            formData.append("ptitle", ptitle);
            formData.append("pbody", pbody);
            formData.append("category", category);
            if (prize) {
                formData.append("prize", parseInt(prize));
            }
            if (coverImage) {
                formData.append("coverImage", coverImage);
            }

            $.ajax({
                url: "/sendPost.do",
                type: "POST",
                data: formData,
                processData: false,
                contentType: false,
                timeout: 120000, // 2分钟超时
                xhr: function() {
                    var xhr = new window.XMLHttpRequest();
                    // 监听上传进度
                    xhr.upload.addEventListener("progress", function(evt) {
                        if (evt.lengthComputable && coverImage) {
                            var percentComplete = Math.round((evt.loaded / evt.total) * 100);
                            loadingAlert.find('small').text('正在上传封面图片... ' + percentComplete + '%');
                        }
                    }, false);
                    return xhr;
                },
                success: function(data) {
                    loadingAlert.remove();
                    $sendButton.html(originalText).prop('disabled', false);

                    if (data == "发送成功") {
                        // 显示成功提示
                        var successAlert = $('<div class="alert alert-success" style="position: fixed; top: 20px; right: 20px; z-index: 9999;">')
                            .html('<span class="glyphicon glyphicon-ok"></span> 发布成功！正在跳转到首页...')
                            .appendTo('body')
                            .fadeIn();

                        setTimeout(function() {
                            location.reload();
                        }, 1500);
                    } else {
                        alert(data);
                        if (data == "你已被禁言") {
                            return false;
                        }
                    }
                },
                error: function(xhr, status, error) {
                    loadingAlert.remove();
                    $sendButton.html(originalText).prop('disabled', false);

                    var errorMsg = "发帖失败：";
                    if (status === "timeout") {
                        errorMsg += "上传超时，请检查网络连接或尝试压缩图片后重试";
                    } else if (xhr.status === 413) {
                        errorMsg += "文件过大，请压缩图片后重试";
                    } else {
                        errorMsg += (xhr.responseText || "网络错误");
                    }

                    alert(errorMsg);
                }
            });
        }
        else {
            alert("注意字数和板块必须选择");
        }
    });
    $("#sendReply").click(function ()
    {
        console.log("sendReply clicked");
        var replyMessage = $("#replyMessage").val();
        var pid = $("#pid").val();
        console.log("replyMessage:", replyMessage, "pid:", pid);
        if (replyMessage.length > 0 && replyMessage.length <= 1000)
        {
            var data = {"replymessage": replyMessage, "post.pid": pid};
            console.log("Sending reply data:", data);
            $.post("/reply.do", data, function (data)
            {
                console.log("Reply response:", data);
                alert(data);
                if (data == "回帖成功")
                    location.reload();
                else if (data == "你已被禁言")
                    return false;
            });
        }
        else
            alert("注意字数");
    })

    $("#deleteReply").click(function ()
    {
        var url = $(this).parent().attr("href");
        $.get(url, function (data)
        {
            alert(data)
            if (data == "删除成功")
                location.reload()
        })
    });

    $("#updatePwd").click(function ()
    {
        var personNewPwd = $("#personNewPwd").val();
        var personConfirmPwd = $("#personConfirmPwd").val();
        if (personNewPwd.length > 6 && personNewPwd.length <= 16)
            if (personNewPwd == personConfirmPwd)
                $("#updateForm").submit();
            else
            {
                alert("两次输入的密码不相符");
                return false;
            }
        else
        {
            alert("新密码长度长度(6,16]")
            return false;
        }

    });

});

function banUser(uid)
{
    $.get("/ban/" + uid, function (data)
    {
        alert(data)
        if (data == "禁言成功")
            location.reload();
    });
}

function unbanUser(uid)
{
    $.get("/unban/" + uid, function (data)
    {
        alert(data);
        if (data == "解禁成功")
            location.reload();
    });
}

function addFavorite(pid)
{
    console.log("Adding favorite for post:", pid);
    $.get("/favorite/add/" + pid)
    .done(function(data) {
        console.log("Add favorite response:", data);
        alert(data);
        if (data == "添加收藏成功" || data.indexOf("成功") > -1) {
            location.reload();
        }
    })
    .fail(function(xhr, status, error) {
        console.error("Add favorite failed:", status, error);
        alert("收藏失败：" + (xhr.responseText || "网络错误"));
    });
}

function removeFavorite(pid)
{
    console.log("Removing favorite for post:", pid);
    $.get("/favorite/remove/" + pid)
    .done(function(data) {
        console.log("Remove favorite response:", data);
        alert(data);
        if (data == "取消收藏成功" || data.indexOf("成功") > -1) {
            location.reload();
        }
    })
    .fail(function(xhr, status, error) {
        console.error("Remove favorite failed:", status, error);
        alert("取消收藏失败：" + (xhr.responseText || "网络错误"));
    });
}

function toggleSticky(pid, action)
{
    var actionText = action === 'sticky' ? '置顶' : '取消置顶';
    if (confirm('确定要' + actionText + '这个帖子吗？')) {
        $.get("/toggleSticky/" + pid + "/" + action)
        .done(function(data) {
            alert(data);
            if (data.indexOf("成功") > -1) {
                location.reload();
            }
        })
        .fail(function(xhr, status, error) {
            alert("操作失败：" + (xhr.responseText || "网络错误"));
        });
    }
}

function toggleReplySticky(rid, pid, action)
{
    var actionText = action === 'sticky' ? '置顶' : '取消置顶';
    if (confirm('确定要' + actionText + '这个回复吗？')) {
        $.get("/toggleReplySticky/" + rid + "/" + pid + "/" + action)
        .done(function(data) {
            alert(data);
            if (data.indexOf("成功") > -1) {
                location.reload();
            }
        })
        .fail(function(xhr, status, error) {
            alert("操作失败：" + (xhr.responseText || "网络错误"));
        });
    }
}

function adminDeleteUser(uid)
{
    console.log("adminDeleteUser called with uid:", uid);
    if (confirm('确定要注销这个用户吗？此操作将删除该用户的所有数据且不可恢复！')) {
        console.log("Sending POST request to /admin/deleteUser/" + uid);
        $.post("/admin/deleteUser/" + uid)
        .done(function(data) {
            console.log("Response received:", data);
            alert(data);
            if (data.indexOf("成功") > -1) {
                location.reload();
            }
        })
        .fail(function(xhr, status, error) {
            console.error("Request failed:", status, error, xhr.responseText);
            alert("注销失败：" + (xhr.responseText || "网络错误"));
        });
    }
}

/**
 * 删除回复功能
 * 支持三种权限：管理员、帖子创建者、回复创建者
 * @param {number} rid 回复ID
 */
function deleteReply(rid) {
    if (confirm('确定要删除这条回复吗？删除后无法恢复！')) {
        $.get("/deleteReply/" + rid)
        .done(function(data) {
            alert(data);
            if (data.indexOf("成功") > -1) {
                location.reload();
            }
        })
        .fail(function(xhr, status, error) {
            alert("删除失败：" + (xhr.responseText || "网络错误"));
        });
    }
}

function login()
{
    var uname = $("#loginUname").val();
    var upwd = $("#loginUpwd").val();
    if (uname == "" || upwd == "")
        return;
    else
    {
        var data = {"uname": uname, "upwd": upwd};
        $.post("/login.do", data, function (data)
        {
            alert(data);
            if (data == "登录成功")
                location.reload();
            else
            {
                // 登录失败
            }
        });
    }
}

/*]]>*/