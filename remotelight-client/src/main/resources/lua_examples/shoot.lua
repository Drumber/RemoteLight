-- =================
color = {255,0,0} -- Change Me
-- =================

pixelNum = strip.ledNumber()
strand = {}
positionL = {1,1,1}
positionR = {1,1,1}
step = {0.16, 0.08, 0.0} -- easing steps 0.0 - 1.0

-- decelerating to zero
function easeOutCubic(t)
	t = t - 1
	return  math.pow(t, 3) + 1
end

-- map eased value to the strip
function lerp(a, b, t)
	return a + (b - a) * t
end

-- initial strand table
for i=1,pixelNum do
	strand[i] = {0,0,0}
end

while true do
	if strip.delayReached() then
		
		-- set last position to black
		for index, position in ipairs(positionL) do
			strand[position] = colorUtil.BLACK()
		end
		for index, position in ipairs(positionR) do
			strand[position] = colorUtil.BLACK()
		end
		
		-- set new position
		for index, value in ipairs(step) do
			positionL[index] = math.floor(pixelNum / 2 - lerp(0, pixelNum / 2 - 1, easeOutCubic(value)))
			positionR[index] = math.floor(lerp(pixelNum / 2 + 1, pixelNum + 1, easeOutCubic(value)))
			
			step[index] = step[index] + 0.02
			if step[index] > 1.0 then
				step[index] = 0
			end
		end
	
		-- set led at 'position' to 'color'
		for index, position in ipairs(positionL) do
			strand[position] = color
		end
		for index, position in ipairs(positionR) do
			strand[position] = color
		end
		
		-- update strip
		strip.show(strand)
	end
end