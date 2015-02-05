/*函数调用部分
 ------------------*/


/**
 *自定义插件部分
 *类似JAVA的写法。这里只定义函数！
 *By zsky 2014.05
 */
(function ($) {

    $.extend({
        zUI: function (option) {
            var zui = $('body').data("zui");
            if (!zui) {
                $('body').data('zui', zui = new zUI(option));
            }
            return zui;
        }
    });

    var zUI = function (options) {
        var DEFAULTS = {
            topMenuSelc: "#module",
            topSettingsSelc:"#settings",
            leftPanelSelc: "#left-panel",
            mainPanelSelc: "#main-panel-wrapper",
            locationPanelSelc: "#localtion-panel",
            processingPanelSelc:"#processing-panel",
            errorPanelSelc:'#error-panel',
            selected_index: 0,
            home_index: 0,
            collapseWidth: "60px",
            leftPanelCollapsed: false,
            allowDuplicatedClick: true
        };

        this.opts = $.extend({}, DEFAULTS, options);
        this._init();
    };

    //私有方法的例子。放在这里可以以后用
    var avc = function (that) {
        alert($(this).html);
    };


    zUI.prototype = {
        constructor: zUI,

        _init: function () {
            //define variables
            this.menu = {
                url:"menu.json",
                list:null
            };
            this.top = {
                '$menuPanel': $(this.opts.topMenuSelc),
                '$settingPanel':$(this.opts.topSettingsSelc),
                //假定panel中含有<a>的<li>为menu item
                '$menu': null
            };
            this.left = {
                '$panel': $(this.opts.leftPanelSelc),
                //假定leftpanel中的第一个class=list-group的DOM元素为menu列表。
                '$menu': $(this.opts.leftPanelSelc).find('.list-group').eq(0),
                '$toggler': $(this.opts.leftPanelSelc + "-toggle"),
                '$arrow': $(this.opts.leftPanelSelc + "-arrow"),
                'width': $(this.opts.leftPanelSelc).css('width')
            };
            this.main = {
                '$panel': $(this.opts.mainPanelSelc),
                contentStack : new Array()
            };
            this.location = {
                '$panel': $(this.opts.locationPanelSelc),
                stack: new Array()
            };
            this.dialog = {
                '$processingPanel' : $(this.opts.processingPanelSelc),
                '$errorPanel' :$(this.opts.errorPanelSelc)
            }
            this.selected = {$topA: null, $leftA: null};

            this.loadMenu().done($.proxy(function(data){
                this._buildNaviMenu(data.menu);
                this._initNaviBar();
                this._initLeftPanel();
                this._initLocationPanel();
                this._initDialog();
                this._initMainPanel();
            },this));
        },

        loadMenu:function(){
            return $.ajax(this.menu.url, {
                type: 'POST',
                data: {'name': 'default'}
            });
        },

        _buildNaviMenu : function(data){
            this.menu.list = data;
            if(data.length==0){
                this.top.$menuPanel.hide();
                this.top.$settingPanel.hide();
                return;
            }else if(data.length==1){
                //如果只有一个菜单项的情况
                if(data[0].uri=='settings.html'){
                    //是settings,隐藏menu
                    this.top.$menuPanel.hide();
                }else{
                    this.top.$settingPanel.hide();
                }
            }
            var zui = this;
            var divider = '<li class="vdivider"/>';
            //menu content str: '<li><a href="1.html"><i class="fa  fa-truck fa-lg"></i>采购管理</a></li>';
            var contentStr = "";
            var menulist = [];
            $.each(data,function(index,elem){
                if(index==data.length-1&&elem.uri=='settings.html'){
                    //创建settingspanel的内容。
                    var settingStr = divider;
                    settingStr +='<li class="dropdown"><a  class="dropdown-toggle"  data-toggle="dropdown">'
                            +'<i class="fa fa-lg '+elem.icon+'"></i>'+elem.name+'</a>';
                    settingStr += '<ul class="dropdown-menu" role="menu">';
                    if(elem.subMenus!=null&&elem.subMenus.length>0){
                        var settinglist = [];
                        $.each(elem.subMenus,function(index,elem){
                            settinglist[index] = '<li><a href="'+elem.uri+'"><i class="fa '+elem.icon+'"></i>'+elem.name+'</a></li>';
                        });
                        settingStr+=settinglist.join('');
                    }
                    settingStr+='</ul></li>';
                    zui.top.$settingPanel.append(settingStr);
                }else{
                    menulist[index] = divider
                        +'<li>'+'<a href="'+elem.uri+'"><i class="fa fa-lg '+elem.icon+'"></i>'+elem.name+'</a></li>';
                }
            });
            contentStr += menulist.join('')+divider;
            this.top.$menuPanel.append(contentStr);
            //加入红色横条
            this.top.$menuPanel.append("<div class='Znavi_slider'/>");
            //将Menu中的链接保存到$menu数组对象中，为初始化准备。
            this.top.$menu = this.top.$menuPanel.find("li:has(a)");

            this._buildLeftMenu(this.opts.selected_index);
        },

        _buildLeftMenu : function(choosedTopMenuItem){
            var zui = this;
            var topMenu = this._getSelectedMenu(choosedTopMenuItem);
            if(topMenu==null) return;
            //找到合适的topMenu，遍历获取leftMenu
            var leftMenuList = topMenu.subMenus;
            if(leftMenuList==null) return;
            var menulist = [];
            $.each(leftMenuList,function(index,MenuItem){
                if(MenuItem.subMenus==null){
                    //没有子菜单，就把当前项当做直接访问菜单项。
                menulist[index] = '<a href="'+MenuItem.uri+'" class="list-group-item"><i class="fa-left-panel-icon '+MenuItem.icon+'"></i><span class="list-text">'+MenuItem.name+'</span></a>';
                }else{
                    //有子菜单项，就把当前想当做一个类型表示，再遍历其子菜单，得到可以访问的菜单项。（如果变态到4级Menu，这里就不考虑了...）
                    var menuItemList = [];
                    menuItemList[0] = '<div class="list-divider">'+MenuItem.name+'</div>';
                    $.each(MenuItem.subMenus,function(index,MenuItem){
                        menuItemList[index+1] = '<a href="'+MenuItem.uri+'" class="list-group-item"><i class="fa-left-panel-icon '+MenuItem.icon+'"></i><span class="list-text">'+MenuItem.name+'</span></a>';
                    });
                    menulist[index] = menuItemList.join('');
                }
            });

            var contentStr = menulist.join('');
            zui.left.$menu.html(contentStr);
        },

        _getSelectedMenu : function(choosedTopMenuItem){
            if($.isNumeric(choosedTopMenuItem)){
                //传入的是menu.list的index
               return this.menu.list[choosedTopMenuItem]
            }else{
                //传入的是一个uri地址，需要自己比较，得到index
                var menuItem=null;
                $.each(this.menu.list,function(index,topMenu) {
                    if(topMenu.uri == choosedTopMenuItem){
                        menuItem = topMenu;
                        return false;
                    }
                });
                return menuItem;
            }
        },

        _initNaviBar: function() {
            if (this.top.$menu.length != 0) {
                //module中存在li则加上选中的style。
                //因为是从数据库读取的module，可能因为没数据造成selected不存在。
                var $selected_li = this.top.$menu.eq(this.opts.selected_index);
                $selected_li.addClass("active");
                //构造辅助的红条选中样式的dom元素
                var $slide_bar = $(".Znavi_slider").css({
                    "left": $selected_li.position().left + "px",
                    "width": $selected_li.outerWidth() + "px",
                    "margin-top": $selected_li.outerHeight() - 2 + "px"
                });


                //mouseover mouseleave 会冒泡.mouseenter mouseout 不会
                //注意会不会有重复进入的bug？
                this.top.$menu.mouseenter(function () {
                    _moveNaviSlider($slide_bar, $(this));
                });
                //焦点移走了，红条跳回到selected元素上。
                this.top.$menuPanel.mouseleave(function () {
                    _moveNaviSlider($slide_bar, $selected_li);
                });


                //注册载入leftMenu的逻辑。和加选中样式。
                //监听事件用on,事件驱动型，更好！
                //全局引用
                var zui = this;
                this.top.$menu.on('click.zUI', 'a', function (e,source) {
                    e.preventDefault();
                    var $cur_Li = $(this).parent('li');
                    if (zui.opts.allowDuplicatedClick || $selected_li[0] != $cur_Li[0]) {
                        if(!e.hasOwnProperty('originalEvent')&&source==='location'){
                            //event is triggered from inner and source is location.
                            zui._removeLeftMenuSelected();
                            //trigger location changed event
                            zui.location.$panel.trigger('locationChanged.zUI', 'top');
                        }else {
                           /* var leftXHR = loadContext(url, $(this).attr("href"),zui);
                            leftXHR.done(function (data) {
                                //choose selected class
                                $selected_li.removeClass("active");
                                $selected_li = $cur_Li.addClass("active");
                                //cache the selected <a> into zui object
                                zui.selected.$topA = $selected_li.children("a:first");
                                //remove left menu selected class.
                                zui._removeLeftMenuSelected();
                                //trigger location changed event
                                zui.location.$panel.trigger('locationChanged.zUI', 'top');
                                //replace left panel content.
                                zui.left.$menu.empty().html(data);
                                //adjust left menu collapse status.
                                zui._changeLeftMenuCollpased();
                            });*/
                            zui._buildLeftMenu($(this).attr("href"));
                            //choose selected class
                            $selected_li.removeClass("active");
                            $selected_li = $cur_Li.addClass("active");
                            //cache the selected <a> into zui object
                            zui.selected.$topA = $selected_li.children("a:first");
                            //remove left menu selected class.
                            zui._removeLeftMenuSelected();
                            //trigger location changed event
                            zui.location.$panel.trigger('locationChanged.zUI', 'top');;
                            //adjust left menu collapse status.
                            zui._changeLeftMenuCollpased();
                        }
                        var mainXHR = loadContext($(this).attr('href'),null,zui);
                        mainXHR.done(function (data) {
                            zui.showMainPage(data,true,true);
                        });
                    }
                });
            }
        },

        //based on parm(collpased) to enlarge or shrink left panel.
        _slideLeftP: function (collpase) {
            if (!collpase) {
                //enlarge~~~~~~~

                //adjust arrow direction
                this.left.$arrow.removeClass("fa-rotate-180");
                //adjust avator panel.
                this.left.$panel.find("div.avator").removeClass("avator-sm");
                //adjust welcome panel.
                this.left.$panel.find("div.welcome").removeClass("welcome-sm").children("span").show();
                //adjust a text.
                this.left.$panel.find("a.list-group-item").each(function () {
                    $(this).children("span.list-text").html($(this).attr("title"));
                    $(this).attr("title", "");
                    //adjust badget
                    $(this).children("span.badge").removeClass("badge-sm");
                    $(this).css("border-bottom",$(this).attr("border-bottom"));
                });
                //adjust divider text.
                this.left.$panel.find("div.list-divider").each(function (index) {
                    if (index == 0) {
                        //显示第一个divider。
                        $(this).show();
                    } else {
                        $(this).text($(this).attr("title"));
                        $(this).attr("title", "");
                    }
                });

                //adjust the main pane
                this.main.$panel.css("marginLeft", this.left.width);
            } else {
                //shrink~~~~~~~~~

                //adjust arrow direction
                this.left.$arrow.addClass("fa-rotate-180");
                //adjust avator panel.
                this.left.$panel.find("div.avator").addClass("avator-sm");
                //adjust welcome panel.
                this.left.$panel.find("div.welcome").addClass("welcome-sm").children("span").hide();
                //adjust a text.
                this.left.$panel.find("a.list-group-item").each(function () {
                    //alert($container.text());
                    $(this).attr("title", $(this).children("span.list-text").text());
                    $(this).children("span.list-text").html("&nbsp;");
                    $(this).attr("border-bottom",$(this).css("border-bottom"));
                    $(this).css("border-bottom","none");
                    //adjust badget
                    $(this).children("span.badge").addClass("badge-sm");
                });
                //adjust divider text.
                this.left.$panel.find("div.list-divider").each(function (index) {
                    if (index == 0) {
                        $(this).hide();
                    } else {
                        $(this).attr("title", $(this).text());
                        $(this).text("");
                    }
                });

                //adjust the main panel
                this.main.$panel.css("marginLeft", this.opts.collapseWidth);
            }

            this.main.$panel.trigger('leftSizeChanged.zui');
        },

        //初始化leftpanel
        _initLeftPanel: function (options) {
            //initial panel collapse.
            this.left.$arrow.click($.proxy(
                function () {
                    this.slideLeftPanel();
                }, this
            ));
            this._initLeftMenu();
        },

        _initLeftMenu: function () {
            var zui = this;
            //bind click event using jquery delegate-event.
            //这样即使leftmenu新增了a，也可以监听的到！
            this.left.$menu.on("click.zUI", "a", function (e) {
                e.preventDefault();
                var $thisA = $(this);
                if (zui.opts.allowDuplicatedClick || zui.selected.$leftA == null || zui.selected.$leftA[0] != $thisA[0]) {
                    var mainXHR = loadContext($thisA.attr('href'), null,zui);
                    mainXHR.done(function (data) {
                        //切换选中
                        if (zui.selected.$leftA != null) {
                            zui.selected.$leftA.removeClass('active');
                        }
                        zui.selected.$leftA = $thisA.addClass('active');
                        //触发location事件。
                        zui.location.$panel.trigger('locationChanged.zUI', 'left');
                        //载入内容
                        zui.showMainPage(data,true,true);
                    });
                }
            });
        },

        _initLocationPanel: function () {
            //相应localtion的改变事件，function内是具体的变化逻辑，在loadContext的时候会出发该事件。
            this.location.$panel.on("locationChanged.zUI", $.proxy(function (e, locationLevel) {
                if (locationLevel == 'top') {
                    //<li><a target="top">我的面板</a></li>
                    this.location.stack.splice(0, this.location.stack.length, "<li><a level=top>" + this.selected.$topA.text() + "</a></li>");
                } else if (locationLevel == 'left') {
                    this.location.stack.splice(1, this.location.stack.length - 1, "<li><a level=left>" + this.selected.$leftA.children('span:first').text() + "</a></li>");
                }
                this.location.$panel.html(this.location.stack.join(''));
                //切换选中
                this.location.$panel.find('a.active').removeClass('active');
                this.location.$panel.find('a:last').addClass('active');
            }, this));

            //注册点击事件
            var zui = this;
            this.location.$panel.on('click.zUI','a',function(){
                var level = $(this).attr('level')
                if(level==='top'){
                    zui.selected.$topA.trigger('click.zUI','location');
                }else if(level==='left'){
                   // zui.selected.$leftA.trigger('click.zUI','location');
                    zui.backToPrev(true);
                }
            });
        },

        /**
         * load进来的LeftPanel是展开样式，这个函数检测如果leftMnue已经shrink
         * 就把样式改写为shrink的。
         * @private
         */
        _changeLeftMenuCollpased: function () {
            if (this.opts.leftPanelCollapsed) {
                this._slideLeftP(true);
            }
        },

        _removeLeftMenuSelected: function(){
            if(this.selected.$leftA!=null){
                this.selected.$leftA.removeClass('active');
                this.selected.$leftA = null;
            }
        },

        _initMainPanel : function(){
            var  zui = this;
            this.main.$panel.on('click.zUI','a[target=main]',function(e){
                e.preventDefault();
                zui.dialog.$errorPanel.modal({
                    remote:$(this).prop('href')
                });
            });
        },

        _initDialog:function(){

           this.dialog.$processingPanel.css('left',
               $(window).width()/2-this.dialog.$processingPanel.outerWidth()/2).hide();

        },

        slideLeftPanel: function () {
            if (this.opts.leftPanelCollapsed) {
                this.left.$panel.stop().animate({width: this.left.width}, 200, 'linear', this._slideLeftP(false));
            } else {
                this.left.$panel.stop().animate({width: this.opts.collapseWidth}, 200, 'linear', this._slideLeftP(true));
            }
            this.opts.leftPanelCollapsed = !this.opts.leftPanelCollapsed;
            this.left.$panel.trigger('leftPanelCollapsed.zui',this.opts.leftPanelCollapsed);
        },

        /**
         * 载入main页面.
         * @param data 要载入的页面/数据
         * @param loadInNew 是否在'新窗口'载入.true：清空main堆栈，替换main中的页面。
         * false:新页面压入main堆栈，main中新页面盖住之前的页面。
         * 现在的逻辑中，点击top/left 使用的是替换。点击main中的链接，使用的是覆盖。
         * @param fx 是否要动画
         * @returns {zUI} 方便链式操作。
         */

        showMainPage: function(data,loadInNew,fx){
            if(loadInNew){
                this.main.$panel.empty();
                this.main.contentStack.splice(0, this.main.contentStack.length);
            }
            var $panelContent = $('<div class="main-panel"></div>').html(data).appendTo(this.main.$panel)
            if(fx){
                $panelContent.css({left:'400px',opacity:0,'z-index':this.main.contentStack.length+1})
                             .transition({left:'0px',opacity:1});
            }
            this.main.contentStack.push($panelContent);
            return this;
        },

        /**
         * mainPanel显示之前一个页面。现在的逻辑是如果在main里有跳转,之前的页面不清楚，当前页面压在之前页面上面
         * 所以回到之前页面就等于是吧当前页面删除掉。
         * 注意：现在的逻辑点击top或者left main跳转是要清出main中的堆栈，所以此函数对于点击top、left造成的影响什么也不做
         * 只是返回链式操作。
         * @param fx 是否要动画 true/false
         * @returns {zUI} 方便链式操作
         * */

        backToPrev:function(fx){
            if(this.main.contentStack.length<1){
                //no preview main panel,just return.
               return this;
            }
            var $currentMainPanel = this.main.contentStack.pop();
            if(fx){
                $currentMainPanel.transition({left:'400px',opacity:0},function(){
                   $currentMainPanel.remove();
                });
            }else{
                $currentMainPanel.remove();
            }
            return this;
        }

    };
    //private method
    function loadContext(url, data,zui) {
        var timeout = null;
        return $.ajax(url, {
            type: 'POST',
            data: {'uri': data},
            //全局上下文，指定会让callback的this变成这个对象。默认是ajax的settings对象。
            context:zui,
            beforeSend: function (jqXHR, settings) {
                timeout = setTimeout(function(){
                    //判断panel是否显示。
                    if(zui.dialog.$processingPanel.is(':hidden')){
                        zui.dialog.$processingPanel.show();
                    }
                },1000);
            },
            complete: function (jqXHR, settings) {
                clearTimeout(timeout);
                if(!zui.dialog.$processingPanel.is(':hidden')) {
                    zui.dialog.$processingPanel.hide();
                }
            },
            error: function (jqXHR, settings) {
                this.dialog.$errorPanel.modal('show').find('.modal-body').html(jqXHR.responseText);
            }

        });
    }


    function _moveNaviSlider($source, $target) {
        if ($source instanceof jQuery && $target instanceof jQuery) {
            $source.stop().animate({
                left: $target.position().left + "px", width: $target.outerWidth() + "px"
                //marginTop:ntab.parent().eq(0).outerHeight()-2+"px",
                //borderBottomColor: "#aaffff"
            }, 1500, 'easeOutElastic'/*'easeOutBack'*/);
        }
    }

    $.fn.extend({
        zSettingBox: function (options) {
            var defaults = {
                show: false,
                box: $("#settingbox"),
                animate_time: 200
            }
            var opt = $.extend({}, defaults, options);
            if (!opt.show) {
                opt.box.hide();
            }
            $(this).click(function () {
                if (opt.show) {
                    opt.box.slideUp(opt.animate_time);
                    opt.show = false;
                } else {
                    opt.box.slideDown(opt.animate_time)
                    opt.show = true;
                }
                return false;
            });
            opt.box.mouseleave(function () {
                $(this).slideUp(opt.animate_time);
                opt.show = false;
            });
        }

    });

})(jQuery);