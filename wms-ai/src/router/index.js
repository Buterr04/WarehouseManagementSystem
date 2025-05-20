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
import ReturnManagement from "@/views/ReturnManagement.vue";
import LoginPage from "@/views/LoginPage.vue";

const routes = [
    { path: '/login', name: 'login', component: LoginPage },
    { path: '/', name: 'home', component: HomePage }, // 将根路径指向 HomePage
    { path: '/product-management', name: 'product-management', component: ProductManagement },
    {
        path: '/purchase-management',
        children: [
            { path: 'purchase-plan', name: 'purchase-plan', component: PurchasePlan, meta: { role: 3 }  },
            { path: 'purchase-order', name: 'purchase-order', component: PurchaseOrder, meta: { role: 3 }  },
            { path: 'create-purchase-order', name: 'create-purchase-order', component: CreatePurchaseOrder, meta: { role: 3 }  },
            { path: 'supplier-management', name: 'supplier-management', component: SupplierManagement, meta: { role: 3 }  },
        ],
    },
    {
        path: '/inventory-management',
        children: [
            { path: 'inventory-in', name: 'inventory-in', component: InventoryIn, meta: { role: 2 }  },
            { path: 'inventory-out', name: 'inventory-out', component: InventoryOut, meta: { role: 2 }  },
            { path: 'inventory-view', name: 'inventory-management', component: InventoryManagement, meta: { role: 2 }  },
            { path: 'return-management', name: 'return-management', component: ReturnManagement, meta: { role: 2 }  },
        ],
    },
    {
        path: '/sales-management',
        children: [
            { path: 'sales-order', name: 'sales-order', component: SalesOrder, meta: { role: 1 }  },
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

export default router;

router.beforeEach((to, from, next) => {
    const user = JSON.parse(localStorage.getItem('user') || 'null'); // ⚠️ 兼容 null

    // 未登录：拦截除登录页以外的所有页面
    if (!user && to.path !== '/login') {
        return next('/login');
    }

    // 有角色限制的页面，且角色不匹配时拦截
    if (to.meta.role && user && user.roleId !== 0 && to.meta.role !== user.roleId) {
        alert('无权限访问该页面');
        return next(false);
    }

    next();
});

