/*函数调用部分
------------------*/



/**
 *自定义插件部分
 *类似JAVA的写法。这里只定义函数！
 *By zsky 2014.05
*/

(function($) {
	$.extend({
		zAutoSize : function(options){

			var defaults={
				bg_src:"../res/images/bg.jpg",
			};
			var opt = $.extend({},defaults,options);
			$('body').prepend("<img src="+opt.bg_src+" id='background'>");
			var bgImg = $("#background");
			
			bgImg.addClass('fullBg');
			
			function resize() {
				var winwidth = $(window).width();
				var winheight = $(window).height();
				
				bgImg.load(function(){
					var imgwidth = bgImg.width();
					var imgheight = bgImg.height();
					
					var widthratio = winwidth/imgwidth;
					var heightratio = winheight/imgheight;
					
					var widthdiff = heightratio*imgwidth;
					var heightdiff = widthratio*imgheight;
					
					if(heightdiff>winheight) {
						bgImg.css({
							width: winwidth+'px',
							height: heightdiff+'px'
						});
					} else {
						bgImg.css({
							width: widthdiff+'px',
							height: winheight+'px'
						});		
					}
				});
				$('#loginFrame').css({'margin-top':(winheight-$('.footer').height())/2-$('#loginFrame').height()/2
					,'margin-left':winwidth/2-320});
				if($("#loginForm").find(".form-group").size()==3){
					$("#loginForm").css('margin-top','10px');
					$("#loginForm").find(".form-group").addClass("form-group-sm");
				}else{
					$("#loginForm").css('margin-top','20px');
				}
			} 
			$(window).resize(resize).trigger("resize"); 
		}//end of zAutoSize.
		
	});
	
	$.fn.zSelectBox = function(options){
		var defaults = {
			text: "请选择语言：",
			opts:[
					{text:"中文版",value:"zh_CN"},
					{text:"English",value:"en_US"}
			     ],
			selected:"zh_CN"
		}
		var opt = $.extend({},defaults,options);
		
		$(this).append("<label class='selectbox_label'>"+opt.text+"</label><div id='selectbox_frame' class='selectbox_frame'></div>");
		$("#selectbox_frame").append("<span id='def_text'></span><span class='selectbox_span'></span>")
			.append(function(index,html){
			var content = "";
			if(opt.opts.length<1) return;
			content += "<ul id='selectbox_ul' class='selectbox_ul'>";
			$.each(opt.opts,function(index,elem){
				content+="<li value='"+elem.value+"'>"+elem.text+"</li>";
				if(opt.selected==elem.value){
					$("#def_text").text(elem.text);
				}
			});
			content+="</ul>";
			return content;
		}).mouseleave(function() {
            $("#selectbox_ul").hide();
        });
		$("#selectbox_ul").hide().children().hover(function(){
				$(this).css({"background":"#bbb", "cursor": "pointer"});
			},function(){
				$(this).removeAttr("style");
		}).click(function() {
			$.ajax({
				  url: "login.html",
				  cache: false,
				  async: false,
				  data: {"lid": $(this).attr("value")},
				  success: function(html){
					  location.href="login.html";
				  }
			});
        });
		$("#selectbox_frame>span").click(function() {
            $("#selectbox_ul").show();
        });
		
		
		//alert(opt.opts[0].text);
	}
})(jQuery)