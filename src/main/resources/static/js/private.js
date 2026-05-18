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

    $("#sendPostButton").click(function ()
    {
        var ptitle = $("#sendPostTitle").val();
        var pbody = $("#sendPostBody").val();
        var category = $("#sendPostCategory").val();
        var prize = $("#sendPostPrize").val();

        if ((ptitle.length > 0 && ptitle.length <= 30) && (pbody.length > 0 && pbody.length < 1000) && category !== "")
        {
            // 如果是问题板块，验证奖励积分
            if (category === "问题") {
                if (!prize || prize < 1 || prize > 10) {
                    alert("问题板块必须设置奖励积分(1-10)");
                    return;
                }
            }

            var data = {"ptitle": ptitle, "pbody": pbody, "category": category};
            if (prize) {
                data.prize = parseInt(prize);
            }

            $.post("/sendPost.do", data, function (data)
            {
                alert(data);
                if (data == "发送成功")
                    location.reload();
                else if (data == "你已被禁言")
                    return false;
            });
        }
        else
            alert("注意字数和板块必须选择");
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