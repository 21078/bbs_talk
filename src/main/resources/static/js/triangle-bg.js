/**
 * Animated Triangle Background
 * Creates a beautiful animated background with moving triangles
 */

class TriangleBackground {
    constructor() {
        this.canvas = document.getElementById('canvas');
        this.ctx = this.canvas.getContext('2d');
        this.triangles = [];
        this.triangleCount = 12;
        this.animationId = null;

        this.init();
    }

    init() {
        this.resize();
        this.createTriangles();
        this.animate();
        window.addEventListener('resize', () => this.resize());
    }

    resize() {
        this.canvas.width = window.innerWidth;
        this.canvas.height = window.innerHeight;
        this.ctx.fillStyle = '#f8f9fa'; // Light background color
    }

    createTriangles() {
        this.triangles = [];
        for (let i = 0; i < this.triangleCount; i++) {
            this.triangles.push(this.createTriangle());
        }
    }

    createTriangle() {
        const size = Math.random() * 80 + 40; // Triangle size between 40-120
        const x = Math.random() * this.canvas.width;
        const y = Math.random() * this.canvas.height;
        const vx = (Math.random() - 0.5) * 0.5; // Horizontal speed
        const vy = (Math.random() - 0.5) * 0.5; // Vertical speed
        const rotationSpeed = (Math.random() - 0.5) * 0.02; // Rotation speed
        const rotation = Math.random() * Math.PI * 2;

        // Random colors with opacity
        const colors = [
            'rgba(13, 110, 253, 0.1)',   // Bootstrap primary blue
            'rgba(255, 193, 7, 0.1)',    // Bootstrap warning yellow
            'rgba(220, 53, 69, 0.1)',    // Bootstrap danger red
            'rgba(25, 135, 84, 0.1)',    // Bootstrap success green
            'rgba(111, 66, 193, 0.1)',   // Bootstrap purple
            'rgba(32, 201, 151, 0.1)'    // Custom teal
        ];

        const color = colors[Math.floor(Math.random() * colors.length)];

        return {
            x, y, size, vx, vy, rotation, rotationSpeed, color
        };
    }

    drawTriangle(triangle) {
        this.ctx.save();
        this.ctx.translate(triangle.x, triangle.y);
        this.ctx.rotate(triangle.rotation);

        this.ctx.beginPath();
        this.ctx.moveTo(0, -triangle.size / 2);
        this.ctx.lineTo(-triangle.size * Math.cos(Math.PI / 6), triangle.size / 4);
        this.ctx.lineTo(triangle.size * Math.cos(Math.PI / 6), triangle.size / 4);
        this.ctx.closePath();

        this.ctx.fillStyle = triangle.color;
        this.ctx.fill();

        // Add a subtle stroke
        this.ctx.strokeStyle = triangle.color.replace('0.1', '0.3');
        this.ctx.lineWidth = 1;
        this.ctx.stroke();

        this.ctx.restore();
    }

    updateTriangle(triangle) {
        // Update position
        triangle.x += triangle.vx;
        triangle.y += triangle.vy;
        triangle.rotation += triangle.rotationSpeed;

        // Bounce off edges
        if (triangle.x < -triangle.size) {
            triangle.x = this.canvas.width + triangle.size;
        } else if (triangle.x > this.canvas.width + triangle.size) {
            triangle.x = -triangle.size;
        }

        if (triangle.y < -triangle.size) {
            triangle.y = this.canvas.height + triangle.size;
        } else if (triangle.y > this.canvas.height + triangle.size) {
            triangle.y = -triangle.size;
        }
    }

    animate() {
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

        // Draw and update all triangles
        this.triangles.forEach(triangle => {
            this.drawTriangle(triangle);
            this.updateTriangle(triangle);
        });

        this.animationId = requestAnimationFrame(() => this.animate());
    }

    destroy() {
        if (this.animationId) {
            cancelAnimationFrame(this.animationId);
        }
    }
}

// Initialize the triangle background when the page loads
document.addEventListener('DOMContentLoaded', function() {
    // Remove the old canvas.js initialization
    const oldCanvas = document.getElementById('canvas');
    if (oldCanvas) {
        // Clear any existing canvas content
        const ctx = oldCanvas.getContext('2d');
        ctx.clearRect(0, 0, oldCanvas.width, oldCanvas.height);

        // Initialize the new triangle background
        new TriangleBackground();
    }
});