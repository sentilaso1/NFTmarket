var newbeeSwiper = new Swiper('.swiper-container', {
    // Set autoplay
    autoplay: {
        delay: 3000,
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
