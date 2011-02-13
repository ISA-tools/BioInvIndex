// JAVASCRIPT FUNCTIONS

// GLOBAL PAGE MOVEMENT
jQuery(document).ready(function() {

    // AUTOMATED SCROLLING
    jQuery('a[href*=#]').click(function() {
        if (location.pathname.replace(/^\//, '') == this.pathname.replace(/^\//, '')
                && location.hostname == this.hostname) {
            var $target = jQuery(this.hash);
            $target = $target.length && $target
                    || jQuery('[name=' + this.hash.slice(1) + ']');
            if ($target.length) {
                var targetOffset = $target.offset().top;
                jQuery('html,body')
                        .animate({scrollTop: targetOffset}, 900);
                return false;
            }
        }
    });

    //toggles info section
    jQuery("#info").hide();
    jQuery("#info_btn").toggle(
            function () {
                jQuery("#info").slideDown("slow");
            },
            function hideEtc() {
                jQuery("#info").slideUp("slow");
            }
            );

    jQuery("#info_btn_ebi_deploy").toggle(
            function () {
                jQuery("#info").slideDown("slow");
            },
            function hideEtc() {
                jQuery("#info").slideUp("slow");
            }
            );


    jQuery('#isatools_icon').hover(function() {
        jQuery(this).animate({opacity: '1'}, 200);
    },
            function() {
                jQuery(this).stop().clearQueue().animate({opacity: '.5'}, 200);
            });
});
