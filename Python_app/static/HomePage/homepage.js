document.body.style.overflow = 'hidden';

let currentSectionIndex = 0;
const sections = document.querySelectorAll('.section')
const links = document.getElementById('navbarNav').querySelectorAll('.custom_link');
const login = document.getElementById('Logowanie');
const register = document.getElementById('Rejestracja');
const totalSections = sections.length;
let isScrolling = false;


document.addEventListener("wheel", (e) => {
    if (isScrolling) return;

    isScrolling = true;

    const upBtn = document.getElementById("UpButton");

    if (e.deltaY > 0) {
        if (upBtn) {
            upBtn.remove()
        }
        currentSectionIndex = Math.min(currentSectionIndex + 1, totalSections - 1);
    } else {
        currentSectionIndex = Math.max(currentSectionIndex - 1, 0);
        if (upBtn) {
            upBtn.remove()
            generateUpBtn(sections[Math.max(currentSectionIndex - 1, 1)])
        } else {
            generateUpBtn(sections[Math.max(currentSectionIndex - 1, 1)])
        }
    }

    sections[currentSectionIndex].scrollIntoView({behavior: 'smooth'});

    setTimeout(() => {
        isScrolling = false;
    }, 800);
}, {passive: false});

function generateUpBtn(section) {
    const div = document.createElement("div");
    div.style.width = "100%";
    div.style.height = "10vh";
    div.style.position = "absolute"
    div.classList.add("d-flex", "justify-content-center", "align-items-center")
    div.id = "UpButton";
    const btn = document.createElement("button")
    btn.id = "UpButton";
    btn.style.zIndex = "1";
    btn.classList.add("btn")
    const img = document.createElement("img")
    img.src = upArrowSrc
    img.style.height = "3em";
    btn.appendChild(img)
    btn.addEventListener('click', (e) => {
        sections[0].scrollIntoView({behavior: 'smooth'});
        currentSectionIndex = 0;
        div.remove();
    })
    div.appendChild(btn)
    section.appendChild(div);
}

function moveToSection(sectionIndex) {
    currentSectionIndex = sectionIndex;
    sections[currentSectionIndex].scrollIntoView({behavior: 'smooth'});
}

Array.from(links).forEach(link => link.addEventListener('click', (e) => {
    switch (e.currentTarget.id) {
        case "Usługi_link":
            moveToSection(2);
            return
        case "Video_link":
            moveToSection(3);
            return;
        case "Cechy_link":
            moveToSection(4);
            return;
        default:
            moveToSection(1);
            return;
    }
}))

login.addEventListener('click', function (e) {
    e.preventDefault();
    window.location.href = '/user/login';
})

register.addEventListener('click', function (e) {
    e.preventDefault();
    window.location.href = '/user/register';
})

document.addEventListener("DOMContentLoaded", function () {
    function equalHeight(selector) {
        let elements = document.querySelectorAll(selector);
        let height = 0;

        elements.forEach((element) => {
            element.style.height = 'auto';
        })

        elements.forEach((element) => {
            if (element.offsetHeight > height) {
                height = element.offsetHeight;
            }
        })

        elements.forEach((element) => {
            element.style.height = height + 'px';
        })
    }

    equalHeight(".defaultCard .card-body .card-title")
    equalHeight(".defaultCard .card-body .card-text")

    window.addEventListener("resize", function () {
        equalHeight(".defaultCard .card-body .card-title")
        equalHeight(".defaultCard .card-body .card-text")
    })
})