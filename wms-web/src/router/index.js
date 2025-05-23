import { createRouter, createWebHistory } from 'vue-router';
import ProductManagement from '../views/ProductManagement.vue';
import PurchasePlan from '../views/PurchasePlan.vue';
import PurchaseOrder from '../views/PurchaseOrder.vue';
import InventoryIn from '../views/InventoryIn.vue';
import InventoryOut from '../views/InventoryOut.vue';
import SalesOrder from '../views/SalesOrder.vue';
import HomePage from '../views/HomePage.vue';
import SalesOrderForm from '../views/SalesOrderForm.vue'
import CustomerMangement from "@/views/CustomerMangement.vue";
import SalesOrderModify from "@/views/SalesOrderModify.vue";
import InventoryManagement from "@/views/InventoryManagement.vue";
import SupplierManagement from "@/views/SupplierManagement.vue";
import CreatePurchaseOrder from "@/views/PurchaseOrderForm.vue";
import LoginPage from "@/views/LoginPage.vue";
import UserManagement from "@/views/UserManagement.vue";
import DeliveryOrder from "@/views/DeliveryOrder.vue";

const routes = [
    { path: '/login', name: 'login', component: LoginPage },
    { path: '/', name: 'home', component: HomePage }, // 将根路径指向 HomePage
    { path: '/product-management', name: 'product-management', component: ProductManagement },
    {
        path: '/system-management',
        children: [
            { path: 'user-management', name: 'user-management', component: UserManagement, meta: { role: 0 } },
        ],
    },
    {
        path: '/purchase-management',
        children: [
            { path: 'purchase-order', name: 'purchase-order', component: PurchaseOrder, meta: { role: 3 }  },
            { path: 'create-purchase-order', name: 'create-purchase-order', component: CreatePurchaseOrder, meta: { role: 3 }  },
            { path: 'supplier-management', name: 'supplier-management', component: SupplierManagement, meta: { role: 3 }  },
        ],
    },
    {
        path: '/inventory-management',
        children: [
            { path: 'purchase-plan', name: 'purchase-plan', component: PurchasePlan, meta: { role: 2 }  },
            { path: 'inventory-in', name: 'inventory-in', component: InventoryIn, meta: { role: 2 }  },
            { path: 'inventory-out', name: 'inventory-out', component: InventoryOut, meta: { role: 2 }  },
            { path: 'inventory-view', name: 'inventory-management', component: InventoryManagement, meta: { role: 2 }  },
        ],
    },
    {
        path: '/sales-management',
        children: [
            { path: 'sales-order', name: 'sales-order', component: SalesOrder, meta: { role: 1 }  },
            { path: 'delivery-order', name: 'delivery-order', component: DeliveryOrder, meta: { role: 1 }  },
        ],
    },
    { path: '/sales-order/create', name: 'CreateSalesOrder', component: SalesOrderForm, meta: { role: 1 }  },
    { path: '/sales-order/edit/:id', name: 'EditSalesOrder', component: SalesOrderModify, props: true, meta: { role: 1 } },
    { path: '/sales-management/customer-management', name: 'customer-management', component: CustomerMangement, meta: { role: 1 }  },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const user = JSON.parse(localStorage.getItem('user') || 'null');

    // 未登录拦截
    if (!user && to.path !== '/login') {
        return next('/login');
    }

    // 遍历所有匹配的路由记录，获取需要的角色权限
    const requiredRole = to.matched.find(r => r.meta?.role)?.meta.role;

    console.log('to.path:', to.path, 'requiredRole:', requiredRole, 'user roleId:', user?.roleId);


    // 如果该页面有限制，且当前用户不是超级管理员也不是匹配角色，则禁止访问
    if (requiredRole !== undefined && user && user.roleId !== 0 && user.roleId !== requiredRole) {
        alert('无权限访问该页面');
        return next(false);
    }

    next();
});


export default router;