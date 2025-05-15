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

const routes = [
    { path: '/', name: 'home', component: HomePage }, // 将根路径指向 HomePage
    { path: '/product-management', name: 'product-management', component: ProductManagement },
    {
        path: '/purchase-management',
        children: [
            { path: 'purchase-plan', name: 'purchase-plan', component: PurchasePlan },
            { path: 'purchase-order', name: 'purchase-order', component: PurchaseOrder },
        ],
    },
    {
        path: '/inventory-management',
        children: [
            { path: 'inventory-in', name: 'inventory-in', component: InventoryIn },
            { path: 'inventory-out', name: 'inventory-out', component: InventoryOut },
            { path: 'inventory-view', name: 'inventory-management', component: InventoryManagement },
        ],
    },
    {
        path: '/sales-management',
        children: [
            { path: 'sales-order', name: 'sales-order', component: SalesOrder },
        ],
    },
    {
        path: '/sales-order/create',
        name: 'CreateSalesOrder',
        component: SalesOrderForm,
    },
    {
        path: '/sales-order/edit/:id',
        name: 'EditSalesOrder',
        component: SalesOrderModify,
        props: true,
    },
    {
        path: '/sales-management/customer-management',
        name: 'customer-management',
        component: CustomerMangement,
    }
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

export default router;