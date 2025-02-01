var newbeeSwiper = new Swiper('.swiper-container', {
    // Set autoplay
    autoplay: {
        delay: 2000,
        disableOnInteraction: false
    },
    // Set infinite loop
    loop: true,
    // Set pagination indicators
    pagination: {
        el: '.swiper-pagination',
    },
    // Set next and previous buttons
    navigation: {
        nextEl: '.swiper-button-next',
        prevEl: '.swiper-button-prev',
    }
});

$('.all-sort-list > .item').hover(function () {
    var eq = $('.all-sort-list > .item').index(this),              // Get the index of the current element being hovered over
        h = $('.all-sort-list').offset().top,                      // Get the distance of the dropdown menu from the top of the window
        s = $(window).scrollTop(),                                 // Get the height scrolled by the browser
        i = $(this).offset().top,                                  // Get the distance of the current element being hovered over from the top of the window
        item = $(this).children('.item-list').height(),            // Get the height of the subcategory content container in the dropdown menu
        sort = $('.all-sort-list').height();                       // Get the height of the parent category list container

    if (item < sort) {                                             // If the height of the subcategory is less than the height of the parent category
        if (eq == 0) {
            $(this).children('.item-list').css('top', (i - h));
        } else {
            $(this).children('.item-list').css('top', (i - h) + 1);
        }
    } else {
        if (s > h) {                                               // Determine the display position of the subcategory. If the scroll height is greater than the height of the entire category list container
            if (i - s > 0) {                                       // Continue to determine the position of the current container being hovered over to avoid the bug of having half outside the window and half inside the window
                $(this).children('.item-list').css('top', (s - h) + 2);
            } else {
                $(this).children('.item-list').css('top', (s - h) - (-(i - s)) + 2);
            }
        } else {
            $(this).children('.item-list').css('top', 3);
        }
    }

    $(this).addClass('hover');
    $(this).children('.item-list').css('display', 'block');
}, function () {
    $(this).removeClass('hover');
    $(this).children('.item-list').css('display', 'none');
});
