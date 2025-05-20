// src/stores/userStore.js
import { ref } from 'vue';

export const user = ref(JSON.parse(localStorage.getItem('user') || 'null'));

export const setUser = (newUser) => {
    user.value = newUser;
    if (newUser) {
        localStorage.setItem('user', JSON.stringify(newUser));
    } else {
        localStorage.removeItem('user');
    }
};
